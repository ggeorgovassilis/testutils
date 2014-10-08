package com.github.georgovassilis.mockitools;

public interface IAsyncBankService {

	interface Callback<T>{
		void onSuccess(T result);
	}
	
	Void findAccountById(String id, Callback<BankAccount> callback);
	
}
