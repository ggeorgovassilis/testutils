mockitools
=================

Support classes that simplify working with mockito [answers](http://docs.mockito.googlecode.com/hg/1.9.5/org/mockito/stubbing/Answer.html).

Read the [original article](http://georgovassilis.blogspot.com/2012/07/compact-mockito-shorter-answer-notation.html).

Mockito answers allow executing complicated code when a mocked method is called as opposed to simply returning values. In order to do so,
you'd normally implement the ```Answer.answer(InvocationOnMock)``` method which leaves you with the cumbersome task of reading method
arguments out of the supplied parameter array.

Mockitools provides the ```BaseAnswer``` class which will delegate the answer call to any method which has the same method signature with the
mocked method.

An example: we need to test some code which depends on a remote ```IAsyncBankService``` which we'll mock for this test.
The service has an ```findAccountById``` method which normally goes to a remote web service, but here we'll just mock it.

```java

public interface IAsyncBankService {
	
	BankAccount findAccountById(String id);
	
}

...

when(bankServiceMock.findAccountById(eq(id))).then(new BaseAnswer<Void>() {
				Void findAccountById(String checkId) {
					BankAccount checkAccount = accountTestDao.find(checkId);
					return checkAccount;
			}
});


```

You can get it by either checking out the project or adding this repository:

```xml
<repository>
	<id>mockitools-mvn-repo</id>
	<url>https://raw.github.com/ggeorgovassilis/testutils/gh-pages/</url>
</repository>
```

and this dependency:

```xml
<dependency>
	<groupId>com.github.ggeorgovassilis</groupId>
	<artifactId>mockitools</artifactId>
	<version>1.0-SNAPSHOT</version>
</dependency>
```
