package it.graphitech.monitor;




import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchService;

public class PathTest extends Thread {
	 
	File file;
	public  boolean  isModified;
	//ShadingDecorator sd;
    @Override
	public void run() {
		// TODO Auto-generated method stub
    	 try {
             System.out.println("Listening on: " + file);
             listenForChanges(file);
         } catch (IOException ex) {
             ex.printStackTrace();
         }
	}

    /*
    
	public void setSd(ShadingDecorator sd) {
		this.sd = sd;
	}

*/

	public void setFile(File file) {
		this.file = file;
	}



	private void listenForChanges(File file) throws IOException {
        Path path = file.toPath();
        if (file.isDirectory()) {
         
        
            WatchService ws = path.getFileSystem().newWatchService();
            path.register(ws, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
            WatchKey watch = null;
            while (true) {
                System.out.println("Watching file: " + file.getPath());
                try {
                    watch = ws.take();
                } catch (InterruptedException ex) {
                    System.err.println("Interrupted");
                }
                java.util.List<WatchEvent<?>> events = watch.pollEvents();
                watch.reset();
                for (WatchEvent<?> event : events) {
                    Kind<Path> kind = (Kind<Path>) event.kind();
                    Path context = (Path) event.context();
                    /*
                    if (kind.equals(StandardWatchEventKinds.OVERFLOW)) {
                        System.out.println("OVERFLOW");
                    } else 
                    	if (kind.equals(StandardWatchEventKinds.ENTRY_CREATE)) {
                        System.out.println("Created: " + context.getFileName());
                    } else 
                    	if (kind.equals(StandardWatchEventKinds.ENTRY_DELETE)) {
                        System.out.println("Deleted: " + context.getFileName());
                    } else 
                    */
                    	if (kind.equals(StandardWatchEventKinds.ENTRY_MODIFY)) {
                    		 if (context.getFileName().endsWith(".vert")) {
                    	            System.out.println("vertex shader has changed");
                    	        }
                    		 if (context.getFileName().endsWith(".frag")) {
                 	            System.out.println("fragment shader has changed");
                 	        }
                    		 if (context.getFileName().endsWith(".geom")) {
                 	            System.out.println("geometry shader has changed");
                 	        }
                        System.out.println("Modified: " + context.getFileName());
                        System.out.println("context: " + context.getFileName());
                        //sd.updateShaders();
                       // sd.isModified=true;
                        isModified=true;
                      System.out.println("metto isModified: "+isModified);
                       
                    }
                }
            }
        } else {
            System.err.println("Not a directory. Will exit.");
        }
        
    }
 /*
    public static void main(String[] args) {
        if (args.length > 0) {
            File file = new File(args[0]);
            try {
                System.out.println("Listening on: " + file);
                listenForChanges(file);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            System.out.println("Pass directory path as parameter");
        }
    }
    */
}