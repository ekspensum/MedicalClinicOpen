import { browser, by, element } from 'protractor';

export class OwnerPage {
  login = element(by.css('input[name=login]'));
  password = element(by.css('input[name=password]'));
  password1 = element(by.css('input[name=password1]'));
  password2 = element(by.css('input[name=password2]'));
  submitLogin = element(by.buttonText('Sign in'));
  submitLogout = element(by.buttonText('Logout'));
  errorMsg = element(by.css('strong'));
  submitUpdate = element(by.buttonText('Save data'));
  streetNo = element(by.css('input[name=streetNo]'));
  confirm = element(by.buttonText('OK'));

  navigateToLoginPage(): Promise<unknown> {
    return browser.get('/login') as Promise<unknown>;
  }
  navigateToSelfEditPage(): Promise<unknown> {
    return browser.get('/owner/selfedit') as Promise<unknown>;
  }
  getLabelLoginText(): Promise<string> {
    return element(by.css('app-login label[for=login]')).getText() as Promise<string>;
  }
  loginAsOwner(login: string, password: string) {
    this.login.sendKeys(login);
    this.password.sendKeys(password);
    this.submitLogin.click();
  }
  updateOwnerData(streetNo: string, password1: string, password2: string) {
    this.password1.sendKeys(password1);
    this.password2.sendKeys(password2);
    this.streetNo.clear();
    this.streetNo.sendKeys(streetNo);
    this.submitUpdate.click();
  }
  confirmAlert(){
    this.confirm.click();
  }
  getUpdatedStreetNo(): Promise<string> {
    return this.streetNo.getAttribute('value') as Promise<string>;
  }
  logout(){
    this.submitLogout.click();
  }
}
