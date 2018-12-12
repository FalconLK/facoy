import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;


public class CreateJavaProject {
	public static IProject CreateJavaProject(String name, IPath classpath) throws CoreException {
		// Create and Open New Project in Workspace
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		IProject project = root.getProject(name);
		project.create(null);
		project.open(null);
		
		// Add Java Nature to new Project
		IProjectDescription desc = project.getDescription();
		desc.setNatureIds(new String[] { JavaCore.NATURE_ID});
		project.setDescription(desc, null);
		
		// Get Java Project Object
		IJavaProject javaProj = JavaCore.create(project);
		
		// Set Output Folder
		IFolder binDir = project.getFolder("bin");
		IPath binPath = binDir.getFullPath();
		javaProj.setOutputLocation(binPath, null);
		
		// Set Project's Classpath
		IClasspathEntry cpe = JavaCore.newLibraryEntry(classpath, null, null);
		javaProj.setRawClasspath(new IClasspathEntry[] {cpe}, null);
		
		return project;
	}
}
