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
package org.ant4eclipse.ant.platform.core;

/**
 * <p>
 * Extends the interfaces {@link ProjectSetComponent} and {@link WorkspaceComponent} and allows to define all projects
 * in the workspace as a project set.
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public interface WorkspaceProjectSetComponent extends ProjectSetComponent, WorkspaceComponent {

  /**
   * <p>
   * Set the 'allProjects' flag. If this flag is set to <code>true</code>, the method
   * {@link ProjectSetComponent#getProjectNames()} returns the names of all projects contained in the underlying
   * workspace (regardless if a project set or project names are defined).
   * </p>
   * 
   * @param allWorkspaceProjects
   *          the flag
   */
  void setAllWorkspaceProjects(boolean allWorkspaceProjects);

  /**
   * <p>
   * Returns the 'allWorkspaceProjects' flag.
   * </p>
   * 
   * @return the 'allWorkspaceProjects' flag.
   */
  boolean isAllWorkspaceProjects();

  /**
   * <p>
   * Throws an exception if neither <code>allWorkspaceProjects</code> nor <code>projectSet</code> nor
   * <code>projectNames</code> are set.
   * </p>
   * 
   */
  void requireAllWorkspaceProjectsOrProjectSetOrProjectNamesSet();
}
