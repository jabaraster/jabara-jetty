package jabara.jetty;

import jabara.general.ExceptionUtil;

import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.plus.webapp.EnvConfiguration;
import org.eclipse.jetty.plus.webapp.PlusConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.FragmentConfiguration;
import org.eclipse.jetty.webapp.MetaInfConfiguration;
import org.eclipse.jetty.webapp.TagLibConfiguration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebInfConfiguration;
import org.eclipse.jetty.webapp.WebXmlConfiguration;

/**
 * @author jabaraster
 */
public class ServerStarter {

    /**
     * 
     */
    public static final String  KEY_WEB_PORT                      = "web.port";                       //$NON-NLS-1$
    /**
     * 
     */
    public static final int     DEFAULT_WEB_PORT                  = 8081;
    /**
     * 
     */
    public static final String  DEFAULT_WEB_APPLICATION_DIRECTORY = "src/main/webapp/";               //$NON-NLS-1$
    /**
     * 
     */
    public static final String  DEFAULT_CONTEXT_PATH              = "/";                              //$NON-NLS-1$

    private int                 webPort                           = getWebPortFromSystemProperty();
    private String              webApplicationDirectory           = DEFAULT_WEB_APPLICATION_DIRECTORY;
    private String              contextPath                       = DEFAULT_CONTEXT_PATH;

    private final AtomicBoolean started                           = new AtomicBoolean(false);

    private Server              server;

    private WebAppContext       webAppContext;

    /**
     * @return contextPathを返す.
     */
    public String getContextPath() {
        return this.contextPath;
    }

    /**
     * @return serverを返す.
     */
    public Server getServer() {
        return this.server;
    }

    /**
     * @return webAppContextを返す.
     */
    public WebAppContext getWebAppContext() {
        return this.webAppContext;
    }

    /**
     * @return webApplicationDirectoryを返す.
     */
    public String getWebApplicationDirectory() {
        return this.webApplicationDirectory;
    }

    /**
     * @return webPortを返す.
     */
    public int getWebPort() {
        return this.webPort;
    }

    /**
     * @param pContextPath contextPathを設定.
     */
    public void setContextPath(final String pContextPath) {
        this.contextPath = pContextPath;
    }

    /**
     * @param pWebApplicationDirectory webApplicationDirectoryを設定.
     */
    public void setWebApplicationDirectory(final String pWebApplicationDirectory) {
        this.webApplicationDirectory = pWebApplicationDirectory;
    }

    /**
     * @param pWebPort webPortを設定.
     */
    public void setWebPort(final int pWebPort) {
        this.webPort = pWebPort;
    }

    /**
     * 
     */
    public void start() {
        synchronized (this.started) {
            if (this.started.get()) {
                throw new IllegalStateException("既にサーバは起動しています."); //$NON-NLS-1$
            }
            try {
                this.server = createServer();
                beforeCreateServer();
                this.server.start();
                this.started.set(true);

            } catch (final Exception e) {
                throw ExceptionUtil.rethrow(e);
            }
        }
    }

    /**
     * 
     */
    public void stop() {
        if (this.server != null) {
            try {
                this.server.stop();
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 
     */
    protected void beforeCreateServer() {
        // デフォルト処理なし
    }

    private WebAppContext createContext(final String pWebAppDirectory) {
        this.webAppContext = new WebAppContext();
        this.webAppContext.setConfigurations(new Configuration[] { //
                new AnnotationConfiguration() //
                        , new WebXmlConfiguration() //
                        , new WebInfConfiguration() //
                        , new TagLibConfiguration() //
                        , new PlusConfiguration() //
                        , new MetaInfConfiguration() //
                        , new FragmentConfiguration() //
                        , new EnvConfiguration() //
                });
        this.webAppContext.setContextPath(this.contextPath);
        this.webAppContext.setResourceBase(pWebAppDirectory);
        this.webAppContext.setParentLoaderPriority(true); // これがないとJARの読み込みに失敗することがある.

        return this.webAppContext;
    }

    private Server createServer() {
        final Server ret = new Server(this.webPort);
        ret.setHandler(createContext(this.webApplicationDirectory));
        return ret;
    }

    private static int getWebPortFromSystemProperty() {
        final String webPort = System.getProperty(KEY_WEB_PORT);
        if (webPort == null) {
            return DEFAULT_WEB_PORT;
        }
        return Integer.parseInt(webPort);
    }

}
