package org.restcomm.perfcorder.rs;

import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.DELETE;

/**
 * REST Web Service
 *
 * @author jim
 */
public class PerfResultResource {

    private String id;

    /**
     * Creates a new instance of PerfResultResource
     */
    private PerfResultResource(String id) {
        this.id = id;
    }

    /**
     * Get instance of the PerfResultResource
     */
    public static PerfResultResource getInstance(String id) {
        // The user may use some kind of persistence mechanism
        // to store and restore instances of PerfResultResource class.
        return new PerfResultResource(id);
    }

    /**
     * Retrieves representation of an instance of org.restcomm.perfcorder.rs.PerfResultResource
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(javax.ws.rs.core.MediaType.APPLICATION_XML)
    public String getXml() {
        //TODO return proper representation object
        throw new UnsupportedOperationException();
    }

    /**
     * PUT method for updating or creating an instance of PerfResultResource
     * @param content representation for the resource
     */
    @PUT
    @Consumes(javax.ws.rs.core.MediaType.APPLICATION_XML)
    public void putXml(String content) {
    }

    /**
     * DELETE method for resource PerfResultResource
     */
    @DELETE
    public void delete() {
    }
}
