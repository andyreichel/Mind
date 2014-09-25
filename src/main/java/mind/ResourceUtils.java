package mind;

public class ResourceUtils {
	public static String stripOfClassNameFromClassId(String classId)
	{
		int slashIndex = classId.lastIndexOf("/");
		if(slashIndex == -1)
			return classId;
		return classId.substring(slashIndex+1);
	}
}
