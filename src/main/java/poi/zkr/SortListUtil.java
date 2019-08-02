package poi.zkr;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;

public class SortListUtil<T> {
    public SortListUtil() {
    }

    public void sortByMethod(List<T> list, final List<String> memberNames, final List<Boolean> reverseFlags) {
        Collections.sort(list, new Comparator<Object>() {
            public int compare(Object arg1, Object arg2) {
                int result = 0;

                for(int i = 0; i < memberNames.size(); ++i) {
                    try {
                        String method = "get" + ((String)memberNames.get(i)).substring(0, 1).toUpperCase() + ((String)memberNames.get(i)).substring(1);
                        Method m1 = arg1.getClass().getMethod(method, (Class[])null);
                        Method m2 = arg2.getClass().getMethod(method, (Class[])null);
                        Object obj1 = m1.invoke(arg1, (Object[])null);
                        Object obj2 = m2.invoke(arg2, (Object[])null);
                        if (obj1 == null && obj2 == null) {
                            result = 0;
                        } else if (obj1 == null && obj2 != null) {
                            result = -1;
                        } else if (obj1 != null && obj2 == null) {
                            result = 1;
                        } else if (obj1 instanceof String) {
                            result = obj1.toString().compareTo(obj2.toString());
                        } else if (obj1 instanceof Date) {
                            long l = ((Date)obj1).getTime() - ((Date)obj2).getTime();
                            if (l > 0L) {
                                result = 1;
                            } else if (l < 0L) {
                                result = -1;
                            } else {
                                result = 0;
                            }
                        } else if (obj1 instanceof Integer) {
                            result = ((Integer)obj1).compareTo((Integer)obj2);
                        } else if (obj1 instanceof Double) {
                            result = ((Double)obj1).compareTo((Double)obj2);
                        } else if (obj1 instanceof BigDecimal) {
                            result = ((BigDecimal)obj1).compareTo((BigDecimal)obj2);
                        } else {
                            result = obj1.toString().compareTo(obj2.toString());
                            System.err.println("SortListUtil.sortByMethod方法接受到不可识别的对象类型，转换为字符串后比较返回...");
                        }

                        if ((Boolean)reverseFlags.get(i)) {
                            result = -result;
                        }
                    } catch (NoSuchMethodException var12) {
                        var12.printStackTrace();
                    } catch (IllegalAccessException var13) {
                        var13.printStackTrace();
                    } catch (InvocationTargetException var14) {
                        var14.printStackTrace();
                    }

                    if (result != 0) {
                        break;
                    }
                }

                return result;
            }
        });
    }

    public void sortByMethod(List<T> list, String memberName, Boolean reverseFlag) {
        List<String> memberNames = new ArrayList();
        memberNames.add(memberName);
        List<Boolean> reverseFlags = new ArrayList();
        reverseFlags.add(reverseFlag);
        this.sortByMethod(list, (List)memberNames, (List)reverseFlags);
    }

    public void sortByMethod(List<T> list, Boolean reverseFlag, String... memberNames) {
        List<String> memberNamesList = new ArrayList();
        List<Boolean> reverseFlags = new ArrayList();

        for(int i = 0; i < memberNames.length; ++i) {
            reverseFlags.add(reverseFlag);
            memberNamesList.add(memberNames[i]);
        }

        this.sortByMethod(list, (List)memberNamesList, (List)reverseFlags);
    }
}
