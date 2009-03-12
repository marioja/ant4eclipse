package org.ant4eclipse.build.tools;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.ResourceCollection;

/**
 * <p>
 * Implements the abstract base class for merge tasks.
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public abstract class AbstractMergeTask extends Task {

  /** the resource collections */
  private List<ResourceCollection> _resourceCollections;

  /** the destination file */
  private File                     _destinationFile;

  /**
   * <p>
   * Creates a new instance of type {@link AbstractMergeTask}.
   * </p>
   */
  public AbstractMergeTask() {

    // create the resource collection list
    _resourceCollections = new LinkedList<ResourceCollection>();
  }

  /**
   * <p>
   * Adds a set of files.
   * </p>
   * 
   * @param set
   *          a set of files.
   */
  public void addFileset(FileSet set) {
    add(set);
  }

  /**
   * <p>
   * Adds a collection of files.
   * </p>
   * 
   * @param resourceCollection
   *          a resource collection.
   */
  public void add(ResourceCollection resourceCollection) {
    _resourceCollections.add(resourceCollection);
  }

  /**
   * <p>
   * Sets the destination file.
   * </p>
   * 
   * @param destinationFile
   *          the destination file.
   */
  public void setDestinationFile(File destinationFile) {
    _destinationFile = destinationFile;
  }

  /**
   * <p>
   * Returns the resource collections.
   * </p>
   * 
   * @return the resource collections.
   */
  public List<ResourceCollection> getResourceCollections() {
    return _resourceCollections;
  }

  /**
   * <p>
   * The destination file.
   * </p>
   * 
   * @return the destination file.
   */
  public File getDestinationFile() {
    return _destinationFile;
  }

  /**
   * @see org.apache.tools.ant.Task#execute()
   */
  public final void execute() throws BuildException {

    // Validates that the task parameters are valid.
    validate();

    try {
      doExecute();
    } catch (Exception e) {
      e.printStackTrace();
      throw new BuildException(e.getMessage(), e);
    }
  }

  /**
   * <p>
   * Abstract method. Subclasses of this class must override this method.
   * </p>
   * 
   * @throws Exception
   */
  protected abstract void doExecute() throws Exception;

  /**
   * <p>
   * Validate that the task parameters are valid.
   * </p>
   * 
   * @throws BuildException
   *           if parameters are invalid
   */
  protected void validate() throws BuildException {

    if (!_destinationFile.canWrite()) {
      try {
        _destinationFile.createNewFile();
      } catch (IOException e) {
        throw new BuildException("Unable to write to " + _destinationFile + ".");
      }
    }
  }
}