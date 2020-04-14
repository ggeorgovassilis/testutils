package com.github.ggeorgovassilis.mockitools;

public class AccountBalanceController {
	
	private IAsyncBankService bankService;
	private IAccountBalanceForm form;
	
	public AccountBalanceController(IAsyncBankService service, IAccountBalanceForm form){
		this.bankService = service;
		this.form = form;
	}
	
	public void onCheckAccountBalanceButtonClicked(){
		form.setMessage("Checking account balance, please wait");
		final String accountId = form.getAccountId();
		bankService.findAccountById(accountId, new IAsyncBankService.Callback<BankAccount>() {
			
			@Override
			public void onSuccess(BankAccount result) {
				if (result == null)
					form.setMessage("We couldn't find account "+accountId);
				else
					form.setMessage("Your account balance is "+result.balance);
			}
		});
	}
}

