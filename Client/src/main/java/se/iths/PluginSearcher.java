package se.iths;


import Spi.*;


import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Optional;
import java.util.ServiceLoader;

public class PluginSearcher {
    private String filerquest=null;
    private String args;
    private Class METHOD =null;
    public PluginSearcher(String args){
        this.args=args;
    }

    public static void main(String[] args) {
   //     PluginSearcher pluginSearcher = new PluginSearcher(args);
  //      pluginSearcher.run();
    }
    public void setFilerquest(String filerquest){
        this.filerquest=filerquest;
    }

    public void setMETHOD(String METHOD) {
        switch (METHOD) {
            case "GET":
                this.METHOD = GET.class;
                break;
            case "POST":
                this.METHOD = POST.class;
                break;
            case "HEAD":
                this.METHOD = HEAD.class;
                break;
        }
    }

    private URLClassLoader createClassLoader(String fileLocation) {
        File loc = new File(fileLocation);

        File[] flist = loc.listFiles(new FileFilter() {
            public boolean accept(File file) {
                return file.getPath().toLowerCase().endsWith(".jar");
            }
        });
        URL[] urls = new URL[flist.length];
        for (int i = 0; i < flist.length; i++) {
            try {
                urls[i] = flist[i].toURI().toURL();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return new URLClassLoader(urls);
    }

    public void run(Response response,Request request) {

        URLClassLoader ucl = createClassLoader(args);

            ServiceLoader<Page> getload =
                    ServiceLoader.load(Page.class, ucl);

                Optional<Page> page = getload
                        .stream()
                        .filter(p -> p.type().isAnnotationPresent(Adress.class))
                        .filter(p -> p.type().getAnnotation(Adress.class).value().equals(request.fileRequested))
                        .map(ServiceLoader.Provider::get).findFirst();
                setMETHOD(request.getMethod().toUpperCase());
                page.ifPresent(
                        pages -> {
                            Method[] methods = pages.getClass().getDeclaredMethods();
                            for (Method m: methods) {
                                if (m.isAnnotationPresent(METHOD)){
                                    try {
                                        m.invoke(pages,request,response);
                                    } catch (IllegalAccessException | InvocationTargetException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                );



    }
}