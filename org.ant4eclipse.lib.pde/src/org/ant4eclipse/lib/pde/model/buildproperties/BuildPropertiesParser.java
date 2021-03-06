/**********************************************************************
 * Copyright (c) 2005-2009 ant4eclipse project team.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Nils Hartmann, Daniel Kasmeroglu, Gerd Wuetherich
 **********************************************************************/
package org.ant4eclipse.lib.pde.model.buildproperties;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.ant4eclipse.lib.core.Assure;
import org.ant4eclipse.lib.core.util.StringMap;
import org.ant4eclipse.lib.pde.internal.model.featureproject.FeatureProjectRoleImpl;
import org.ant4eclipse.lib.pde.model.buildproperties.PluginBuildProperties.Library;
import org.ant4eclipse.lib.pde.model.featureproject.FeatureProjectRole;
import org.ant4eclipse.lib.pde.model.pluginproject.PluginProjectRole;
import org.ant4eclipse.lib.platform.model.resource.EclipseProject;

/**
 * <p>
 * The {@link BuildPropertiesParser} reads and interprets the <code>build.properties</code> file of an eclipse plug-in
 * Project.
 * </p>
 * <p>
 * The properties can be received from an opened EclipseProject using:
 * 
 * <pre>
 *   PluginBuildProperties buildProperties =
 *       PluginProjectRole.getPluginProjectRole(eclipseProject).getBuildProperties()
 * </pre>
 * 
 * @see PluginProjectRole#getBuildProperties()
 */
public class BuildPropertiesParser {

  /** -- */
  public static final String BUILD_PROPERTIES = "build.properties";

  /**
   * Reads the build.properties of the given eclipse project. After the properties have been read they are assigned to
   * the project's PluginRole and can be accessed using {@link PluginProjectRole#getBuildProperties()}.
   * 
   * <p>
   * <b>Note</b>: <code>The build.properties</code> is automatically read when an EclipseProject containing the
   * PluginRole is opened. <b>There is normally no need to call this method otherwise.</b>
   * 
   * @param project
   *          The eclipse project. Has to be a plugin project
   * @throws FileParserException
   * @see PluginProjectRole#getBuildProperties()
   */
  public static void parsePluginBuildProperties(PluginProjectRole pluginProjectRole) {
    Assure.notNull("pluginProjectRole", pluginProjectRole);
    StringMap properties = loadBuildProperties(pluginProjectRole.getEclipseProject());
    PluginBuildProperties buildProperties = initializePluginBuildProperties(properties);
    pluginProjectRole.setBuildProperties(buildProperties);
  }

  /**
   * Reads the build.properties of the given eclipse project. After the properties have been read they are assigned to
   * the project's PluginRole and can be accessed using {@link PluginProjectRole#getBuildProperties()}.
   * 
   * <p>
   * <b>Note</b>: <code>The build.properties</code> is automatically read when an EclipseProject containing the
   * PluginRole is opened. <b>There is normally no need to call this method otherwise.</b>
   * 
   * @param featureProjectRole
   *          the featureProjectRole
   * @see PluginProjectRole#getBuildProperties()
   */
  public static void parseFeatureBuildProperties(FeatureProjectRole featureProjectRole) {
    Assure.notNull("featureProjectRole", featureProjectRole);

    StringMap buildProperties = loadBuildProperties(featureProjectRole.getEclipseProject());
    FeatureBuildProperties featureBuildProperties = new FeatureBuildProperties();
    initializeAbstractBuildProperties(buildProperties, featureBuildProperties);
    // TODO
    ((FeatureProjectRoleImpl) featureProjectRole).setBuildProperties(featureBuildProperties);
  }

  private static StringMap loadBuildProperties(EclipseProject eclipseProject) {
    Assure.notNull("eclipseProject", eclipseProject);
    File file = eclipseProject.getChild(BUILD_PROPERTIES);
    return new StringMap(file);
  }

  /**
   * Initializes the buildProperties with the build properties read from the given input stream (typically a
   * FileInputStream to a "build.properties" file). The input stream must be processable via java.util.Property's
   * load(InputStream) method.
   * 
   * <p>
   * This method is mainly public to make it accessible from tests (?)
   * </p>
   */
  public static PluginBuildProperties initializePluginBuildProperties(StringMap properties) {
    Assure.notNull("properties", properties);

    PluginBuildProperties buildProperties = new PluginBuildProperties();
    initializeAbstractBuildProperties(properties, buildProperties);

    // set src.includes
    String includes = properties.get("src.includes", "");
    buildProperties.setSourceIncludes(getAsList(includes, ",", true));

    // set src.excludes
    String excludes = properties.get("src.excludes", "");
    buildProperties.setSourceExcludes(getAsList(excludes, ",", true));

    // set jars.compile.order
    String jarsCompileOrder = properties.get("jars.compile.order");
    if (jarsCompileOrder != null) {
      buildProperties.setJarsCompileOrder(getAsList(jarsCompileOrder, ",", true));
    }

    // set source and target compatibility level
    String javacSource = properties.get("javacSource", "1.3");
    buildProperties.setJavacSource(javacSource);
    String javacTarget = properties.get("javacTarget", "1.2");
    buildProperties.setJavacTarget(javacTarget);

    // Set additional Bundles property
    String additionalBundles = properties.get("additional.bundles");
    if (additionalBundles != null) {
      buildProperties.setAdditionalBundles(getAsList(additionalBundles, ",", false));
    }

    // set libraries
    Iterator<?> iterator = properties.keySet().iterator();
    while (iterator.hasNext()) {
      String key = (String) iterator.next();

      if (key.startsWith("source.")) {
        String libraryName = key.substring("source.".length());

        Library library = new Library(libraryName);

        String[] source = getAsList(properties.get("source." + libraryName), ",", true);
        library.setSource(source);

        String[] output = getAsList(properties.get("output." + libraryName), ",", true);
        library.setOutput(output);

        String manifest = properties.get("manifest." + libraryName);
        library.setManifest(manifest);

        String exclude = properties.get("exclude." + libraryName);
        library.setExclude(exclude);

        buildProperties.addLibrary(library);

        String extraKey = "extra." + libraryName;
        properties.get(extraKey);
      }
    }
    return buildProperties;
  }

  private static void initializeAbstractBuildProperties(StringMap allProperties,
      AbstractBuildProperties abstractBuildProperties) {
    Assure.notNull("allProperties", allProperties);
    Assure.notNull("abstractBuildProperties", abstractBuildProperties);

    // set qualifier
    abstractBuildProperties.setQualifier(allProperties.get("qualifier"));

    // set custom
    abstractBuildProperties.setCustom(Boolean.valueOf(allProperties.get("custom", "false")).booleanValue());

    // set bin.includes
    String includes = allProperties.get("bin.includes", "");
    abstractBuildProperties.setBinaryIncludes(getAsList(includes, ",", true));

    // set bin.excludes
    String excludes = allProperties.get("bin.excludes", "");
    abstractBuildProperties.setBinaryExcludes(getAsList(excludes, ",", true));
  }

  /**
   * @param content
   * @param delimiter
   * @return
   */
  private static String[] getAsList(String content, String delimiter, boolean removePathSeparator) {
    Assure.notNull("delimiter", delimiter);

    if (content == null) {
      return new String[] {};
    }

    List<String> result = new LinkedList<String>();

    StringTokenizer tokenizer = new StringTokenizer(content, delimiter);
    while (tokenizer.hasMoreTokens()) {
      String token = tokenizer.nextToken().trim();
      if (removePathSeparator && (token.length() > 1) && token.endsWith("/")) {
        token = token.substring(0, token.length() - 1);
      }
      result.add(token);
    }

    return result.toArray(new String[0]);
  }
}
