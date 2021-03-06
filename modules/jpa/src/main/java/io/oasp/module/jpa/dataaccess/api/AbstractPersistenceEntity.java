package io.oasp.module.jpa.dataaccess.api;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.persistence.Version;

/**
 * Abstract base implementation of {@link MutablePersistenceEntity} with a {@link GeneratedValue generated}
 * {@link #getId() primary key}. In case you need a different type of key add it as extra column and make it
 * {@link javax.persistence.Column#unique() unique}.
 *
 * @deprecated will be removed in a future release. In order to give OASP users more flexibility we want to stop
 *             providing an JPA entity base class in this library. Instead we provide it with our application template
 *             (oasp4j-template-server) so you can take over control of JPA annotations. If you already started with
 *             OASP in an earlier version you can simply update `ApplicationPersistenceEntity` from our current sample
 *             on github to get rid of the dependency to this class.
 *
 * @author hohwille
 * @author rjoeris
 */
@MappedSuperclass
@Deprecated
public abstract class AbstractPersistenceEntity implements MutablePersistenceEntity<Long> {

  private static final long serialVersionUID = 1L;

  /** @see #getId() */
  private Long id;

  /** @see #getModificationCounter() */
  private int modificationCounter;

  /** @see #getRevision() */
  private Number revision;

  /**
   * The constructor.
   */
  public AbstractPersistenceEntity() {

    super();
  }

  @Override
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  public Long getId() {

    return this.id;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setId(Long id) {

    this.id = id;
  }

  @Override
  @Version
  public int getModificationCounter() {

    return this.modificationCounter;
  }

  @Override
  public void setModificationCounter(int version) {

    this.modificationCounter = version;
  }

  @Override
  @Transient
  public Number getRevision() {

    return this.revision;
  }

  /**
   * @param revision the revision to set
   */
  @Override
  public void setRevision(Number revision) {

    this.revision = revision;
  }

  @Override
  public String toString() {

    StringBuilder buffer = new StringBuilder();
    toString(buffer);
    return buffer.toString();
  }

  /**
   * Method to extend {@link #toString()} logic.
   *
   * @param buffer is the {@link StringBuilder} where to {@link StringBuilder#append(Object) append} the string
   *        representation.
   */
  protected void toString(StringBuilder buffer) {

    buffer.append(getClass().getSimpleName());
    if (this.id != null) {
      buffer.append("[id=");
      buffer.append(this.id);
      buffer.append("]");
    }
  }
}
