package com.github.georgovassilis.mockitools;

import org.junit.Before;
import org.junit.Test;

import com.github.georgovassilis.mockitools.IAsyncBankService.Callback;

import static org.mockito.Mockito.*;

/**
 * This test tests the BaseAnswer by constructing the following scenario:
 * 
 * We want to test a short bank-account-balance-check workflow. The class under
 * test is {@link AccountBalanceController} which depends on a UI form (
 * {@link IAccountBalanceForm} ) and a remote, asynchronous service (
 * {@link IAsyncBankService} ). Since we, supposedly, want to test only the
 * controller, we'll construct mocks for the form and the service and inject
 * these mocks to the controller.
 * 
 * The controller will read the bank account ID from the form and ask the remote
 * bank service to return the balance. Since it's an asynchronous call, the
 * controller has to provide a callback method to the service. That's where the
 * {@link BaseAnswer} comes in by gluing together the callback and the mocked
 * service code.
 * 
 * @author george georgovassilis
 *
 */
public class TestBaseAnswer {

	AccountBalanceController accountBalanceController;
	IAsyncBankService bankServiceMock;
	IAccountBalanceForm accountBalanceFormMock;

	@Before
	public void setup() {
		bankServiceMock = mock(IAsyncBankService.class);
		accountBalanceFormMock = mock(IAccountBalanceForm.class);
		accountBalanceController = new AccountBalanceController(
				bankServiceMock, accountBalanceFormMock);
	}

	@Test
	public void testBankServiceWithdrawal_with_existing_account() {
		final String id = "12345";
		final BankAccount account = new BankAccount();
		account.id = id;
		account.balance = 100;
		account.customerName = "Test Customer";

		when(accountBalanceFormMock.getAccountId()).thenReturn(id);
		when(bankServiceMock.findAccountById(eq(id), any(Callback.class)))
				.then(new BaseAnswer<Void>() {

					Void findAccountById(String checkId,
							Callback<BankAccount> callback) {

						BankAccount checkAccount = null;
						if (id.equals(checkId))
							checkAccount = account;
						callback.onSuccess(checkAccount);
						return null;
					}

				});

		accountBalanceController.onCheckAccountBalanceButtonClicked();
		verify(accountBalanceFormMock)
				.setMessage("Your account balance is 100");
	}

	@Test
	public void testBankServiceWithdrawal_with_inexistent_account() {
		final String id = "12345";
		final String fauxId = "000";
		final BankAccount account = new BankAccount();
		account.id = id;
		account.balance = 100;
		account.customerName = "Test Customer";

		when(accountBalanceFormMock.getAccountId()).thenReturn(fauxId);
		when(bankServiceMock.findAccountById(any(String.class), any(Callback.class)))
				.then(new BaseAnswer<Void>() {

					Void findAccountById(String checkId,
							Callback<BankAccount> callback) {

						BankAccount checkAccount = null;
						if (id.equals(checkId))
							checkAccount = account;
						callback.onSuccess(checkAccount);
						return null;
					}

				});

		accountBalanceController.onCheckAccountBalanceButtonClicked();
		verify(accountBalanceFormMock).setMessage(
				"We couldn't find account "+fauxId);
	}
}
