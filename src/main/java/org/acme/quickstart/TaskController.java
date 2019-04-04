package org.acme.quickstart;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.persistence.EntityManager;
import javax.ws.rs.*;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.annotations.jaxrs.PathParam;

@Path("tasks")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class TaskController {

    @Inject
    EntityManager entityManager;

    @GET
    public Task[] get() {
        return entityManager.createNamedQuery("Tasks.findAll", Task.class)
                .getResultList().toArray(new Task[0]);
    }

    @GET
    @Path("{id}")
    public Task getSingle(@PathParam("id") Integer id) {
        Task entity = entityManager.find(Task.class, id);
        if (entity == null) {
            throw new WebApplicationException("Task with id of " + id + " does not exist.", 404);
        }
        return entity;
    }

    @POST
    @Transactional
    public Response create(Task task) {
        if (task.getId() != null) {
            throw new WebApplicationException("Id was invalidly set on request.", 422);
        }

        entityManager.persist(task);
        return Response.ok(task).status(201).build();
    }

    @PUT
    @Path("{id}")
    @Transactional
    public Task update(@PathParam("id") Integer id, Task task) {
        if (task.getName() == null) {
            throw new WebApplicationException("Task Name was not set on request.", 422);
        }

        Task entity = entityManager.find(Task.class, id);

        if (entity == null) {
            throw new WebApplicationException("Task with id of " + id + " does not exist.", 404);
        }

        entity.setName(task.getName());

        return entity;
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public Response delete(@PathParam("id") Integer id) {
        Task entity = entityManager.getReference(Task.class, id);
        if (entity == null) {
            throw new WebApplicationException("Task with id of " + id + " does not exist.", 404);
        }
        entityManager.remove(entity);
        return Response.status(204).build();
    }

    @Provider
    public static class ErrorMapper implements ExceptionMapper<Exception> {

        @Override
        public Response toResponse(Exception exception) {
            int code = 500;
            if (exception instanceof WebApplicationException) {
                code = ((WebApplicationException) exception).getResponse().getStatus();
            }
            return Response.status(code)
                    .entity(Json.createObjectBuilder().add("error", exception.getMessage()).add("code", code).build())
                    .build();
        }

    }
}
