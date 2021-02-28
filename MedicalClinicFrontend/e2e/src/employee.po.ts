import { browser, by, element } from 'protractor';

export class EmployeePage {
  login = element(by.css('input[name=login]'));
  password = element(by.css('input[name=password]'));
  password1 = element(by.css('input[name=password1]'));
  password2 = element(by.css('input[name=password2]'));
  submitLogin = element(by.buttonText('Sign in'));
  submitLogout = element(by.buttonText('Logout'));
  errorMsg = element(by.css('strong'));
  submitUpdate = element(by.buttonText('Save data'));
  phone = element(by.css('input[name=phone]'));
  confirm = element(by.buttonText('OK'));

  navigateToLoginPage(): Promise<unknown> {
    return browser.get('/login') as Promise<unknown>;
  }
  navigateToSelfEditPage(): Promise<unknown> {
    return browser.get('/employee/selfedit') as Promise<unknown>;
  }
  getLabelLoginText(): Promise<string> {
    return element(by.css('app-login label[for=login]')).getText() as Promise<string>;
  }
  loginAsEmployee(login: string, password: string) {
    this.login.sendKeys(login);
    this.password.sendKeys(password);
    this.submitLogin.click();
  }
  updateEmployeeData(phone: string, password1: string, password2: string) {
    this.password1.sendKeys(password1);
    this.password2.sendKeys(password2);
    this.phone.clear();
    this.phone.sendKeys(phone);
    this.submitUpdate.click();
  }
  confirmAlert(){
    this.confirm.click();
  }
  getUpdatedStreetNo(): Promise<string> {
    return this.phone.getAttribute('value') as Promise<string>;
  }
  logout(){
    this.submitLogout.click();
  }
  consoleLog(text: string){
    element(by.id('view-container')).getText().then(function(text) {
      console.log(text);
    });
  }
}
