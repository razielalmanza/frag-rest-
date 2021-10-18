/*  1:   */
package dgac.utilidades.org.jam;

/*  2:   */
/*  3:   */ public class ChangeInfo
/* 4: */ {

	/* 5: */ private Object parentLeft;

	/* 6: */ private Object parentRight;

	/* 7: */ private Object from;

	/* 8: */ private Object to;

	/* 9: */ private ChangeType changeType;

	/* 10: */ private String fieldName;

	/* 11: */
	/* 12: */ public Object getFrom()
	/* 13: */ {
		/* 14:15 */ return this.from;
		/* 15: */ }

	/* 16: */
	/* 17: */ public void setFrom(Object from)
	/* 18: */ {
		/* 19:19 */ this.from = from;
		/* 20: */ }

	/* 21: */
	/* 22: */ public Object getTo()
	/* 23: */ {
		/* 24:23 */ return this.to;
		/* 25: */ }

	/* 26: */
	/* 27: */ public void setTo(Object to)
	/* 28: */ {
		/* 29:27 */ this.to = to;
		/* 30: */ }

	/* 31: */
	/* 32: */ public ChangeType getChangeType()
	/* 33: */ {
		/* 34:31 */ return this.changeType;
		/* 35: */ }

	/* 36: */
	/* 37: */ public void setChangeType(ChangeType changeType)
	/* 38: */ {
		/* 39:35 */ this.changeType = changeType;
		/* 40: */ }

	/* 41: */
	/* 42: */ public void setFieldName(String name)
	/* 43: */ {
		/* 44:39 */ this.fieldName = name;
		/* 45: */ }

	/* 46: */
	/* 47: */ public String getFieldName()
	/* 48: */ {
		/* 49:43 */ return this.fieldName;
		/* 50: */ }

	/* 51: */
	/* 52: */ public Object getParentLeft()
	/* 53: */ {
		/* 54:47 */ return this.parentLeft;
		/* 55: */ }

	/* 56: */
	/* 57: */ public void setParentLeft(Object parentLeft)
	/* 58: */ {
		/* 59:51 */ this.parentLeft = parentLeft;
		/* 60: */ }

	/* 61: */
	/* 62: */ public Object getParentRight()
	/* 63: */ {
		/* 64:55 */ return this.parentRight;
		/* 65: */ }

	/* 66: */
	/* 67: */ public void setParentRight(Object parentRight)
	/* 68: */ {
		/* 69:59 */ this.parentRight = parentRight;
		/* 70: */ }
	/* 71: */

	}

/*
 * Location: C:\Users\maverick\Downloads\jettison-1.0.1-SNAPSHOT.jar
 *
 * Qualified Name: org.jam.ChangeInfo
 *
 * JD-Core Version: 0.7.0.1
 *
 */