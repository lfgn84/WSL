package se.iths;


import Spi.Adress;
import Spi.Page;
import Spi.PageProvider;

import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Optional;
import java.util.ServiceLoader;

public class PluginSearcher {
    private String filerquest=null;
    private String[] args;
    public PluginSearcher(String[] args){
        this.args=args;
    }

    public static void main(String[] args) {
        PluginSearcher pluginSearcher = new PluginSearcher(args);
        pluginSearcher.run();
    }
    public void setFilerquest(String filerquest){
        this.filerquest=filerquest;
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

    public void run() {

        URLClassLoader ucl = createClassLoader(args[0]);

        ServiceLoader<Page> loader =
                ServiceLoader.load(Page.class, ucl);

        for (Page page : loader) {
            page.execute();
        }


        ServiceLoader<PageProvider> loaders =
                ServiceLoader.load(PageProvider.class, ucl);
        if(!(filerquest == null)) {
            //if (PageProvider.class.getAnnotation(Adress.class).value().equals(filerquest))
                for(PageProvider pp:loaders){
                    if(pp.getClass().isAnnotationPresent(Adress.class) && pp.getClass().getAnnotation(Adress.class).value().equals(filerquest)){
                        pp.executes();
                    }
                }
            //  Requires java >9
         //   {
              /*  Optional<PageProvider> page = loaders
                        .stream()
                        .filter(p -> p.type().isAnnotationPresent(Adress.class)
                                && p.type().getAnnotation(Adress.class).value().equals(filerquest))
                        .map(ServiceLoader.Provider::get).findFirst();

                page.get().executes();*/

                //  for (Page p : page) {
                //      p.execute();
                // }
         //   }
        }
    }
}