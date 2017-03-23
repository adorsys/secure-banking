package org.adorsys.psd2.hbci.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.adorsys.psd2.hbci.domain.EncryptedHbciLoadAccountRequest;
import org.adorsys.psd2.hbci.domain.EncryptedHbciLoadBookingsRequest;
import org.adorsys.psd2.hbci.domain.EncryptedListOfHbciBankAccounts;
import org.adorsys.psd2.hbci.domain.EncryptedListOfHbciBookings;
import org.adorsys.psd2.hbci.domain.HbciLoadAccountsRequest;
import org.adorsys.psd2.hbci.domain.HbciLoadBookingsRequest;
import org.adorsys.psd2.xs2a.domain.EncryptedAccountStatement;
import org.adorsys.psd2.xs2a.utils.JWEUtils;

import domain.BankAccount;
import domain.Booking;
import hbci4java.Hbci4JavaBanking;
import hbci4java.OnlineBankingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import io.swagger.annotations.ResponseHeader;

@Api(value = "/v1/hbci", tags={"Access To Account HBCI"}, authorizations = @Authorization(value = "BearerToken"), description = "HBCI Frontend access to payment data")
@Path("/v1/hbci")
public class HbciResource {

	OnlineBankingService onlineBankingService = new Hbci4JavaBanking();

	@POST
	@Path("accounts")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Load accounts", notes = "Load all bank accounts associated with given baking acess data")
	@ApiResponses(value = { @ApiResponse (code = 200, message = "Ok", response=EncryptedListOfHbciBankAccounts.class),
			@ApiResponse(code = 400, message = "Bad request", responseHeaders=@ResponseHeader(name="ERROR_KEY", description="BAD_REQUEST"))})
	public Response loadBankAccounts(@ApiParam(value="The encrypted bank access object") EncryptedHbciLoadAccountRequest encryptedRequest) {
		HbciLoadAccountsRequest request = JWEUtils.decrypt(encryptedRequest.getJweString(), HbciLoadAccountsRequest.class);
		Optional<List<BankAccount>> bankAccounts = onlineBankingService.loadBankAccounts(request.getBankAccess(), request.getPin());
		ArrayList<BankAccount> bancAccountList = new ArrayList<>();
		if(bankAccounts.isPresent()){
			bancAccountList.addAll(bankAccounts.get());
		}
		String encryptedJwe = JWEUtils.encrypt(bancAccountList, request);
		EncryptedListOfHbciBankAccounts resp = new EncryptedListOfHbciBankAccounts();
		resp.setJweString(encryptedJwe);
		return Response.ok().entity(resp).build();
	}

	@POST
	@Path("bookings")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Load bookings", notes = "Load bookings associated with given bank account")
	@ApiResponses(value = { @ApiResponse (code = 200, message = "Ok", response=EncryptedAccountStatement.class),
			@ApiResponse(code = 400, message = "Bad request", responseHeaders=@ResponseHeader(name="ERROR_KEY", description="BAD_REQUEST"))})
	public Response loadPostings(@ApiParam(value="The encrypted bank access object") EncryptedHbciLoadBookingsRequest encryptedRequest) {
		HbciLoadBookingsRequest request = JWEUtils.decrypt(encryptedRequest.getJweString(), HbciLoadBookingsRequest.class);
		Optional<List<Booking>> bookings = onlineBankingService.loadBookings(request.getBankAccess(), request.getBankAccount(), request.getPin());
		ArrayList<Booking> bookingList = new ArrayList<>();
		if(bookings.isPresent()){
			bookingList.addAll(bookings.get());
		}
		String encryptedJwe = JWEUtils.encrypt(bookingList, request);
		EncryptedListOfHbciBookings resp = new EncryptedListOfHbciBookings();
		resp.setJweString(encryptedJwe);
		return Response.ok().entity(resp).build();
	}
}
