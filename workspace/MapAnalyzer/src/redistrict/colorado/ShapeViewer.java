/*
 * The viewer is an interactive tool to load aand display a ESRI Shape file. The display
 * makes use of Google maps and an embedded web server.
 *  
 * Copyright (C) 2019 Charles Coughlin
 * 
 * This program is free software; you msy redistribute it and/or
 * modify it under the terms of the GNU General Public License.
 */
package redistrict.colorado;

import static com.vividsolutions.jump.workbench.JUMPWorkbench.LOGGER;
import static com.vividsolutions.jump.workbench.JUMPWorkbench.commandLine;
import static com.vividsolutions.jump.workbench.JUMPWorkbench.fixLookAndFeel;
import static com.vividsolutions.jump.workbench.JUMPWorkbench.initLookAndFeel;
import static com.vividsolutions.jump.workbench.JUMPWorkbench.main;
import static com.vividsolutions.jump.workbench.JUMPWorkbench.parseCommandLine;
import static com.vividsolutions.jump.workbench.JUMPWorkbench.printProperly;
import static com.vividsolutions.jump.workbench.JUMPWorkbench.printProperties;
import static com.vividsolutions.jump.workbench.JUMPWorkbench.progressMonitorClass;
import static com.vividsolutions.jump.workbench.JUMPWorkbench.setFont;

import java.util.Arrays;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.locationtech.jts.util.StringUtil;

import com.vividsolutions.jump.I18N;
import com.vividsolutions.jump.JUMPVersion;
import com.vividsolutions.jump.util.commandline.Option;
import com.vividsolutions.jump.workbench.JUMPWorkbench;
import com.vividsolutions.jump.workbench.SplashPanelV2;
import com.vividsolutions.jump.workbench.ui.ErrorDialog;
import com.vividsolutions.jump.workbench.ui.WorkbenchFrame;

public class ShapeViewer extends JUMPWorkbench {

}

public static void main(String[] args) {
    long start = PlugInManager.milliSecondsSince(0);
    try {
      // first fetch parameters, locale might be changed with -i18n switch
      parseCommandLine(args);
      // load i18n specified in command line ( '-i18n translation' )
      if (commandLine.hasOption(I18N_FILE)) {
        I18N_SETLOCALE = commandLine.getOption(I18N_FILE).getArg(0);
        // initialize I18N
        I18N.setLocale(I18N_SETLOCALE);
      }

      // set user agent used by UrlConnection, if not set on cmdline
      if (System.getProperty("http.agent") == null)
        System.setProperty("http.agent", I18N.get("JUMPWorkbench.jump") + " "
            + JUMPVersion.CURRENT_VERSION);

      if (commandLine.hasOption("help")) {
        printProperly(commandLine.printDoc());
        System.exit(0);
      } else if (commandLine.hasOption("version")) {
        printProperly(I18N.get("JUMPWorkbench.jump") + " "
            + I18N.get("ui.AboutDialog.version") + " "
            + JUMPVersion.CURRENT_VERSION);
        System.exit(0);
      } else if (commandLine.hasOption("print-properties")) {
        printProperties("args[]=" + Arrays.toString(args));
        System.exit(0);
      }
      
      // set logging level according to parameter
      if (commandLine.hasOption("verbosity")) {
        Option v = commandLine.getOption("verbosity");
        if (v.getNumArgs() < 1) {
          printProperly(I18N.get(v.getSpec().getDesc()));
          System.exit(1);
        }
          
        Logger.setLevel(v.getArg(0));
      }
      
      // Init the L&F before instantiating the progress monitor [Jon Aquino]
      initLookAndFeel();
      // fix lnf (weird windows non-unicode locale bug)
      fixLookAndFeel();

      // setFont to switch fonts if defaults cannot display current language
      // early change the default font definition of the jre if necessary, the
      // first internationalized string shown is 'JUMPWorkbench.version' on
      // splashpanel
      setFont();

      ProgressMonitor progressMonitor = (ProgressMonitor) progressMonitorClass
          .newInstance();
      SplashPanelV2 splashPanel = new SplashPanelV2(splashImage(),
          I18N.get("ui.AboutDialog.version") + " "
              + JUMPVersion.CURRENT_VERSION);
//      splashPanel.add(progressMonitor, new GridBagConstraints(0, 10, 1, 1, 1,
//          0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
//          new Insets(0, 0, 0, 10), 0, 0));
      splashPanel.addProgressMonitor(progressMonitor);

      main(args, I18N.get("JUMPWorkbench.jump"), splashPanel, progressMonitor);
      LOGGER.info("OJ start took "
          + PlugInManager.secondsSinceString(start) + "s alltogether.");

    } catch (final Throwable t) {
      try {
        SwingUtilities.invokeAndWait(new Runnable() {
          public void run() {
            try {
              initLookAndFeel();
            } catch (Exception e) {
              // fail silently
            }
            ErrorDialog.show(null,
                StringUtil.toFriendlyName(t.getClass().getSimpleName()),
                WorkbenchFrame.toMessage(t), StringUtil.stackTrace(t));
          }
        });
      } catch (Throwable t2) {
        LOGGER.severe(t2.getLocalizedMessage());
      }
      LOGGER.severe(t.getLocalizedMessage());
      System.exit(1);
    }
  }

