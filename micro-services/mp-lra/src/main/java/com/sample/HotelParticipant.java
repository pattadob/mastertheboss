package com.sample;

 
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.lra.annotation.Compensate;
import org.eclipse.microprofile.lra.annotation.Complete;
import org.eclipse.microprofile.lra.annotation.ws.rs.LRA;
 

import java.util.Collection;

import static org.eclipse.microprofile.lra.annotation.ws.rs.LRA.LRA_HTTP_CONTEXT_HEADER;

@Singleton
@Path("/lra")
@LRA(LRA.Type.SUPPORTS)
public class HotelParticipant {
    public static final String HOTEL_PARTICIPANT_PATH = "/hotel-participant";
    public static final String TRANSACTION_COMPLETE = "/complete";
    public static final String TRANSACTION_COMPENSATE = "/compensate";

    @Inject
    private HotelService service;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LRA(value = LRA.Type.REQUIRED, end = false)
    public Booking bookRoom(@HeaderParam(LRA_HTTP_CONTEXT_HEADER) String lraId,
                            @QueryParam("hotelName") @DefaultValue("Default") String hotelName) {

                                System.out.println("Booked "+lraId);
        return service.book(lraId, hotelName);
    }

    @PUT
    @Path(TRANSACTION_COMPLETE)
    @Produces(MediaType.APPLICATION_JSON)
    @Complete
    public Response completeWork(@HeaderParam(LRA_HTTP_CONTEXT_HEADER) String lraId) throws NotFoundException  {
        service.get(lraId).setStatus("confirmed");
        System.out.println("Completed "+lraId);
        return Response.ok(service.get(lraId)).build();
    }

    @PUT
    @Path(TRANSACTION_COMPENSATE)
    @Produces(MediaType.APPLICATION_JSON)
    @Compensate
    public Response compensateWork(@HeaderParam(LRA_HTTP_CONTEXT_HEADER) String lraId) throws NotFoundException  {
        service.get(lraId).setStatus("cancelled");
        return Response.ok(service.get(lraId)).build();
    }

    @GET
    @Path("/{bookingId}")
    @Produces(MediaType.APPLICATION_JSON)
    @LRA(LRA.Type.NOT_SUPPORTED)
    public Booking getBooking(@PathParam("bookingId") String bookingId)   {
        return service.get(bookingId);
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Booking> getAll() {
        return service.getAll();
    }
}