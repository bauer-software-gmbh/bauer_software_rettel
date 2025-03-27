package de.bauersoft.components.container;

public enum ContainerState
{
    /**
     * SHOW indicates that the container is based on a persisted entity.
     * We use "SHOW" to signal that the container should be visible but not necessarily updated, as no changes were made.
     *
     * For example, if we load a set of entities from the database and create a container for each of them,
     * we would use the SHOW state to indicate that the container represents a persisted entity and no changes were made.
     *
     * The TempState should be set to SHOW
     */
    SHOW,

    /**
     * HIDE means that the container's entity should be deleted, but since it has not been persisted yet,
     * we mark the container as hidden instead of deleting it.
     *
     * For example, if a container is in the NEW state, meaning it holds a newly created entity that has not been persisted yet,
     * and we want to remove that container, we use the HIDE state to mark it as hidden rather than using DELETE,
     * which would attempt to delete a non-persisted entity.
     *
     * The TempState should be set to HIDE
     */
    HIDE,

    /**
     * UPDATE means that the container's entity will be persisted to the database regardless of whether any changes were made or not.
     *
     * For example, if a container holds a persisted or not yet persisted entity, but we want to persist or update the entity,
     * we use the UPDATE state to signal the MapContainer that it should persist the entity of that container to the database.
     *
     * The TempState should be set to DELETE
     */
    UPDATE,

    /**
     * DELETE means that the container's entity will be removed from the database, whether it exists or not.
     *
     * For example, if we have a container with an already persisted entity and we want to delete it from the database,
     * we use the DELETE state to signal the MapContainer that it should delete the entity of that container from the database.
     *
     * The TempState should be set to DELETE
     */
    DELETE,


    /**
     * IGNORE is a cosmetic state used to indicate that the container should be ignored in further processing.
     *
     * The TempState should be set to IGNORE
     */
    IGNORE,

    /**
     * DISABLED is similar to IGNORE but with a more meaningful name, indicating that the container will be ignored with a clearer intention.
     *
     * The TempState should be set to DISABLED
     */
    DISABLED,

    /**
     * NEW indicates that the container holds a newly created entity that has not been persisted yet.
     *
     * For example, when the user presses a button that creates a new container holding a newly created, unpersisted entity,
     * we use the NEW state to indicate that the entity has not been persisted yet.
     *
     * The TempState should be set to UPDATE
     */
    NEW;


    public boolean isVisible()
    {
        return this == SHOW || this == UPDATE;
    }

    public boolean isHidden()
    {
        return this == HIDE || this == DELETE;
    }

    public boolean isIgnored()
    {
        return this == IGNORE || this == DISABLED;
    }
}
