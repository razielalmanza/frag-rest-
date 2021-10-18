package dgac.utilidades.org.jam;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

import dgac.utilidades.org.jam.utils.SetObjectInfo;
import dgac.utilidades.org.jam.utils.SetUtils;

public class Diff4J {

	static {
		Properties props = new Properties();
		// try
		// {
		// props.load(ClassLoader.getSystemResourceAsStream("log4j.properties"));
		//
		// PropertyConfigurator.configure(props);
		// }
		// catch (IOException e)
		// {
		// System.out.println("Could not configure log4j");
		// e.printStackTrace();
		// }
	}

	private static final Logger logger = Logger.getLogger(Diff4J.class);

	private int depth;

	private Set visited;

	private Collection<ChangeInfo> changes;

	public Diff4J() {
		this.depth = -1;

		this.visited = new HashSet();

		this.changes = new LinkedList();
	}

	public Collection<ChangeInfo> diff(Object left, Object right) throws IllegalAccessException {
		handleObject(new TypeInfo(left, left.getClass(), null, null),
				new TypeInfo(right, right.getClass(), null, null));

		return this.changes;
	}

	static class TypeInfo {

		Object obj;

		Class<?> clz;

		Object parent;

		Field field;

		TypeInfo(Object obj, Class<?> clz, Object parent, Field field) {
			this.obj = obj;
			this.clz = clz;
			this.parent = parent;
			this.field = field;
		}

	}

	private void handleObject(TypeInfo left, TypeInfo right) throws IllegalAccessException {
		this.depth += 1;
		try {
			if (left.obj != null) {// lfmm 12/06/2018
				if (!this.visited.contains(left.obj)) {
					this.visited.add(left.obj);
					try {
						if (isPrimitiveOrWrapper(left.clz)) {
							handlePrimitive(left, right);
						}
						else if (isCollection(left.clz)) {
							handleCollection(left, right);
						}
						else if (isMap(left.clz)) {
							handleMap(left, right);
						}
						else {
							handleClass(left, right);
						}
					}
					finally {
						this.visited.remove(left.obj);
					}
				}
			}
		}
		finally {
			this.depth -= 1;
		}
	}

	private void handleClass(TypeInfo left, TypeInfo right) throws IllegalAccessException {
		if (logger.isDebugEnabled()) {
			logger.debug(getTabs(this.depth) + "Class: " + left.clz.getName());
		}
		if (left.obj != null) {
			Class<?> clz = left.clz;
			while (clz != Object.class) {
				Field[] fields = clz.getDeclaredFields();
				for (Field field : fields) {
					int modifiers = field.getModifiers();
					if ((!Modifier.isStatic(modifiers)) && (!Modifier.isFinal(modifiers))) {
						Object fieldObj1 = getObject(field, left.obj);
						Object fieldObj2 = getObject(field, right.obj);
						handleObject(new TypeInfo(fieldObj1, field.getType(), left.obj, field),
								new TypeInfo(fieldObj2, field.getType(), right.obj, field));
					}
				}
				clz = clz.getSuperclass();
			}
		}
	}

	private static Object getObject(Field field, Object parent) throws IllegalAccessException {
		try {
			return field.get(parent);
		}
		catch (IllegalAccessException e) {
			field.setAccessible(true);
			return field.get(parent);
		}
	}

	private void handleCollection(TypeInfo left, TypeInfo right) throws IllegalAccessException {
		if (logger.isDebugEnabled()) {
			logger.debug(getTabs(this.depth) + "depth: " + this.depth + " Collection: " + left.field.getName());
		}
		/* INICIA agregado por Luis Felipe Maciel Mercado 25/11/2015 */
		if (this.depth > 0)
			return;
		/* TERMINA agregado por Luis Felipe Maciel Mercado 25/11/2015 */
		Class<?> colType = getParameterizedType(left.field);

		Collection col1 = (Collection) left.obj;
		Collection col2 = (Collection) right.obj;

		Collection<SetObjectInfo<?>> disjoint = SetUtils.disjoint(col1, col2);
		for (SetObjectInfo<?> objInfo : disjoint) {
			ChangeInfo change = new ChangeInfo();
			change.setFieldName(left.field.getName());
			change.setParentLeft(left.parent);
			change.setParentRight(right.parent);
			switch (objInfo.from.ordinal()) {
			case 1:
				change.setFrom(objInfo.obj);
				change.setChangeType(ChangeType.REMOVE);
			case 2:
				change.setTo(objInfo.obj);
				change.setChangeType(ChangeType.ADD);
			}
			this.changes.add(change);
		}
		Map<Integer, ?> col2Map = SetUtils.getIdentityMap(col2);

		Collection<?> intersection = SetUtils.intersection(col1, col2);
		for (Object leftObj : intersection) {
			Object rightObj = col2Map.get(Integer.valueOf(leftObj.hashCode()));
			handleObject(new TypeInfo(leftObj, colType, null, null), new TypeInfo(rightObj, colType, null, null));
		}
	}

	public static Class<?> getParameterizedType(Field field) {
		Type type = field.getGenericType();
		if ((type instanceof ParameterizedType)) {
			ParameterizedType pType = (ParameterizedType) type;
			Type[] typeArgs = pType.getActualTypeArguments();
			if ((typeArgs != null) && (typeArgs.length > 0) && ((typeArgs[0] instanceof Class))) {
				return (Class) typeArgs[0];
			}
		}
		return null;
	}

	private void handlePrimitive(TypeInfo left, TypeInfo right) {
		if (logger.isDebugEnabled()) {
			StringBuilder output = new StringBuilder();

			output.append(getTabs(this.depth)).append("Field: ").append(left.field.getName()).append(" Value: ")
					.append(left.obj.toString());

			logger.debug(output.toString());
		}
		ChangeInfo change = null;
		if (left.obj == null) {
			if (right.obj != null) {
				change = new ChangeInfo();
				change.setChangeType(ChangeType.ADD);
				change.setTo(right.obj);
				change.setFieldName(left.field.getName());
				change.setParentLeft(left.parent);
				change.setParentRight(right.parent);
			}
		}
		else if (right.obj != null) {
			if (!left.obj.equals(right.obj)) {
				change = new ChangeInfo();
				change.setChangeType(ChangeType.CHANGE);
				change.setFrom(left.obj);
				change.setTo(right.obj);
				change.setFieldName(left.field.getName());
				change.setParentLeft(left.parent);
				change.setParentRight(right.parent);
			}
		}
		else {
			change = new ChangeInfo();
			change.setChangeType(ChangeType.REMOVE);
			change.setFrom(left.obj);
			change.setFieldName(left.field.getName());
			change.setParentLeft(left.parent);
			change.setParentRight(right.parent);
		}
		if (change != null) {
			this.changes.add(change);
		}
	}

	private static String getTabs(int depth) {
		StringBuilder tabs = new StringBuilder();
		for (int i = 0; i < depth; i++) {
			tabs.append('\t');
		}
		return tabs.toString();
	}

	private static boolean isPrimitiveOrWrapper(Class<?> clz) {
		return (clz.isPrimitive()) || (Number.class.isAssignableFrom(clz)) || (Boolean.class.isAssignableFrom(clz))
				|| (Character.class.isAssignableFrom(clz)) || (String.class.isAssignableFrom(clz));
	}

	private static boolean isCollection(Class<?> clz) {
		return Collection.class.isAssignableFrom(clz);
	}

	/**
	 * @author Luis Felipe Maciel Mercado lfmm
	 * @param clz
	 * @return boolean
	 */
	private static boolean isList(Class<?> clz) {
		return List.class.isAssignableFrom(clz);
	}

	/**
	 * @author Luis Felipe Maciel Mercado lfmm
	 * @param clz
	 * @return boolean
	 */
	private static boolean isArrayList(Class<?> clz) {
		return ArrayList.class.isAssignableFrom(clz);
	}

	private static boolean isMap(Class<?> clz) {
		return Map.class.isAssignableFrom(clz);
	}

	private void handleMap(TypeInfo info, TypeInfo right) {
	}

}
