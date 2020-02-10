package se.iths;


import spi.Page;



import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ServiceLoader;

public class PluginSearcher {

    public static void main(String[] args) {
        PluginSearcher pluginSearcher = new PluginSearcher();
        pluginSearcher.run(args);
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

    private void run(String[] args) {

        URLClassLoader ucl = createClassLoader(args[0]);

         ServiceLoader<Page> loader =
             ServiceLoader.load(Page.class, ucl);

         for (Page page : loader){
             page.execute();
         }

    }
}