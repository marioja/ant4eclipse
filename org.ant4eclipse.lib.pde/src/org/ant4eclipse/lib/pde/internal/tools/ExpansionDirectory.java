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
package org.ant4eclipse.lib.pde.internal.tools;

import java.io.File;

/**
 * <p>
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public class ExpansionDirectory {

  /** the default expansion directory **/
  public static final String DEFAULT_EXPANSION_DIRECTORY       = System.getProperty("java.io.tmpdir")
                                                                   + File.separatorChar + "a4e_expand_dir";

  /** the name of the expansion directory property **/
  public static final String EXPANSION_DIRECTORY_PROPERTY_NAME = "a4e.expansion.directory";

  /** - */
  private static File        expansionDir                      = null;

  /**
   * <p>
   * </p>
   * 
   * @return
   */
  public static synchronized File getExpansionDir() {

    if (expansionDir == null) {

      String expansionDirectory = System.getProperty(ExpansionDirectory.EXPANSION_DIRECTORY_PROPERTY_NAME,
          ExpansionDirectory.DEFAULT_EXPANSION_DIRECTORY);

      expansionDir = new File(expansionDirectory);

      expansionDir.deleteOnExit();

      if (!expansionDir.exists()) {
        expansionDir.mkdirs();
      }
    }

    return expansionDir;
  }
}
