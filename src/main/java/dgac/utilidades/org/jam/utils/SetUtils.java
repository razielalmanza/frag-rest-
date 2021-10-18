/*  1:   */
package dgac.utilidades.org.jam.utils;

/*  2:   */
/*  3:   */ import java.util.Collection;
/*  4:   */ import java.util.HashMap;
/*  5:   */ import java.util.HashSet;
/*  6:   */ import java.util.Map;
/*  7:   */ import java.util.Set;

/*  8:   */
/*  9:   */ public class SetUtils
/* 10: */ {

	/* 11: */ public static <T> Collection<SetObjectInfo<T>> disjoint(Collection<T> col1, Collection<T> col2)
	/* 12: */ {
		/* 13:27 */ Set<SetObjectInfo<T>> disjoint = new HashSet();
		/* 14:29 */ if (col1 == null)
		/* 15: */ {
			/* 16:30 */ if (col2 != null) {
				/* 17:31 */ for (T obj : col2) {
					/* 18:32 */ disjoint.add(new SetObjectInfo(DisjointInfo.RIGHT, obj));
					/* 19: */ }
				/* 20: */ }
			/* 21: */ }
		/* 22:35 */ else if (col2 != null)
		/* 23: */ {
			/* 24:36 */ for (T obj : col1) {
				/* 25:37 */ if (!col2.contains(obj)) {
					/* 26:38 */ disjoint.add(new SetObjectInfo(DisjointInfo.LEFT, obj));
					/* 27: */ }
				/* 28: */ }
			/* 29:40 */ for (T obj : col2) {
				/* 30:41 */ if (!col1.contains(obj)) {
					/* 31:42 */ disjoint.add(new SetObjectInfo(DisjointInfo.RIGHT, obj));
					/* 32: */ }
				/* 33: */ }
			/* 34: */ }
		/* 35: */ else
		/* 36: */ {
			/* 37:45 */ for (T obj : col1) {
				/* 38:46 */ disjoint.add(new SetObjectInfo(DisjointInfo.LEFT, obj));
				/* 39: */ }
			/* 40: */ }
		/* 41:49 */ return disjoint;
		/* 42: */ }

	/* 43: */
	/* 44: */ public static <T> Collection<T> intersection(Collection<T> col1, Collection<T> col2)
	/* 45: */ {
		/* 46:65 */ Set<T> intersection = new HashSet();
		/* 47:67 */ if ((col1 != null) && (col2 != null)) {
			/* 48:68 */ for (T obj : col1) {
				/* 49:69 */ if (col2.contains(obj)) {
					/* 50:70 */ intersection.add(obj);
					/* 51: */ }
				/* 52: */ }
			/* 53: */ }
		/* 54:75 */ return intersection;
		/* 55: */ }

	/* 56: */
	/* 57: */ public static <V> Map<Integer, V> getIdentityMap(Collection<V> col)
	/* 58: */ {
		/* 59:79 */ Map<Integer, V> identityMap = new HashMap();
		/* 60:80 */ for (V obj : col) {
			/* 61:81 */ identityMap.put(Integer.valueOf(obj.hashCode()), obj);
			/* 62: */ }
		/* 63:83 */ return identityMap;
		/* 64: */ }
	/* 65: */

	}

/*
 * Location: C:\Users\maverick\Downloads\jettison-1.0.1-SNAPSHOT.jar
 *
 * Qualified Name: org.jam.utils.SetUtils
 *
 * JD-Core Version: 0.7.0.1
 *
 */