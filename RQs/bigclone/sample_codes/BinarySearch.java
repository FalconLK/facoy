
public class BinarySearch {
	//Heuristic: (([0-9,a-z,A-Z,_,$]+\s*\=\s*\(\s*[0-9,a-z,A-Z,_,$]+\s*\+\s*[0-9,a-z,A-Z,_,$]+\s*\)\s*/\s*2\s*;)|([0-9,a-z,A-Z,_,$]+\s*=\s*[0-9,a-z,A-Z,_,$]+\s*\+\s*\(\s*[0-9,a-z,A-Z,_,$]+\s*-\s*[0-9,a-z,A-Z,_,$]+\s*\)\s*/\s*2\s*;))
	// imid = imin + (imax - imin)/2;
	// imid = (imax + imin)/2;
	public static int binarySearch1(int arr[], int key, int imin, int imax) {
		//Implementation: Recursive, primitive type
		if(imax < imin)
			return -1;
		int imid = (imin+imax)/2;
		if(arr[imid] > key)
			return binarySearch1(arr,key,imin,imid-1);
		else if (arr[imid] < key)
			return binarySearch1(arr,key,imid+1,imax);
		else
			return imid;		
	}
	
	public static <T extends Comparable<T>> int binarySearch3(T[] arr, T key, int imin, int imax) {
		//Implementation: Recursive, comparable type
		if(imax < imin)
			return -1;
		int imid = (imin+imax)/2;
		if(arr[imid].compareTo(key) > 0)
			return binarySearch3(arr,key,imin,imid-1);
		else if (arr[imid].compareTo(key) < 0)
			return binarySearch3(arr,key,imid+1,imax);
		else
			return imid;
	}

	public static int binarySearch2(int arr[], int key) {
		//Implementation: Iterative, primitive type.
		int imin = 0;
		int imax = arr.length - 1;
		while(imin <= imax) {
			int imid = imin + (imax - imin)/2;
			if (key < arr[imid])
				imax = imid-1;
			else if (key > arr[imid])
				imin = imid + 1;
			else
				return imid;
		}
		return -1;
	}
	
	public static <T extends Comparable<T>> int binarySearch4(T[] arr, T key) {
		int imin = 0;
		int imax = arr.length - 1;
		while(imin <= imax) {
			int imid = imin + (imax - imin)/2;
			if (key.compareTo(arr[imid]) < 0)//(key < arr[imid])
				imax = imid-1;
			else if (key.compareTo(arr[imid]) > 0)//(key > arr[imid])
				imin = imid + 1;
			else
				return imid;
		}
		return -1;
	}
}
