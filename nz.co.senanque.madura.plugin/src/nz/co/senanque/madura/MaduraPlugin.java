/*******************************************************************************
 * Copyright (c)2014 Prometheus Consulting
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package nz.co.senanque.madura;
import java.io.IOException;
import java.net.URL;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
/**
 * The main plugin class.
 * @author Roger Parkinson
 */
public class MaduraPlugin extends AbstractUIPlugin
{
	// The plug-in ID
	public static final String PLUGIN_ID = "nz.co.senanque.madura.plugin"; //$NON-NLS-1$

	//The shared instance.
	private static MaduraPlugin plugin;
	//Resource bundle.
	private ResourceBundle resourceBundle;
	private TokenManager tokenManager;
	public static final String PREF_COMMENT_COLOR = "madura_comment_color";
	public static final String PREF_TEXT_COLOR = "madura_text_color";
	public static final String PREF_CODE_COLOR = "madura_code_color";
	public static final String PREF_KEYWORD_COLOR = "madura_keyword_color";
	public static final String PREF_DEFAULT_COLOR = "madura_default_color";

	public MaduraPlugin()
	{
		super();
		plugin = this;
		try
		{
			resourceBundle =
				ResourceBundle.getBundle(
					"nz.co.senanque.madura.MaduraPluginResources");
		}
		catch (MissingResourceException x)
		{
			x.printStackTrace();
			resourceBundle = null;
		}
		tokenManager = new TokenManager(getPreferenceStore());
	}
	/**
	 * Returns the shared instance.
	 */
	public static MaduraPlugin getDefault()
	{
		return plugin;
	}
	/**
	 * @param project
	 */
	public static void registerBuilder(IProject project, String builderId)
	{
		try
		{
			IProjectDescription desc = project.getDescription();
			ICommand[] commands = desc.getBuildSpec();
			boolean found = false;
			
			for (int i = 0; i < commands.length; ++i) {
			   if (commands[i].getBuilderName().equals(builderId)) {
				  found = true;
				  break;
			   }
			}
			if (!found) { 
			   //add builder to project
			   ICommand command = desc.newCommand();
			   command.setBuilderName(builderId);
			   ICommand[] newCommands = new ICommand[commands.length + 1];
			
			   // Add it before other builders.
			   System.arraycopy(commands, 0, newCommands, 1, commands.length);
			   newCommands[0] = command;
			   desc.setBuildSpec(newCommands);
			   project.setDescription(desc, null);
			}
		}
		catch (CoreException e)
		{
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		configureLogbackInBundle(context.getBundle());
		super.start(context);
		plugin = this;
	}
    private void configureLogbackInBundle(Bundle bundle) throws JoranException, IOException {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        JoranConfigurator jc = new JoranConfigurator();
        jc.setContext(context);
        context.reset();

//        // overriding the log directory property programmatically
//        String logDirProperty = "./conf";// ... get alternative log directory location
//        context.putProperty("LOG_DIR", logDirProperty);//.putProperty("LOG_DIR", logDirProperty);

        // this assumes that the logback.xml file is in the root of the bundle.
        URL logbackConfigFileUrl = FileLocator.find(bundle, new Path("logback.xml"),null);
        jc.doConfigure(logbackConfigFileUrl.openStream());
}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}
	/**
	 * Returns the workspace instance.
	 */
	public static IWorkspace getWorkspace()
	{
		return ResourcesPlugin.getWorkspace();
	}
	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 */
	public static String getResourceString(String key)
	{
		ResourceBundle bundle = MaduraPlugin.getDefault().getResourceBundle();
		try
		{
			return bundle.getString(key);
		}
		catch (MissingResourceException e)
		{
			return key;
		}
	}
	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle()
	{
		return resourceBundle;
	}
	public TokenManager getTokenManager()
	{
		return tokenManager;
	}
}
