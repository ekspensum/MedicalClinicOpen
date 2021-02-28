import { browser, by, element } from 'protractor';

export class AdminPage {
  login = element(by.css('input[name=login]'));
  password = element(by.css('input[name=password]'));
  password1 = element(by.css('input[name=password1]'));
  password2 = element(by.css('input[name=password2]'));
  clinicFreeDay = element(by.id('clinicFreeDay'));
  submitLogin = element(by.buttonText('Sign in'));
  submitLogout = element(by.buttonText('Logout'));
  submitSaveData = element(by.buttonText('Save data'));
  submitDeleteFreeDay = element(by.buttonText('Delete'));
  errorMsg = element(by.css('strong'));
  submitUpdate = element(by.buttonText('Save data'));
  phone = element(by.css('input[name=phone]'));
  confirm = element(by.buttonText('OK'));

  navigateToLoginPage(): Promise<unknown> {
    return browser.get('/login') as Promise<unknown>;
  }
  navigateToSelfEditPage(): Promise<unknown> {
    return browser.get('/admin/selfedit') as Promise<unknown>;
  }
  navigateToClinicFreeDaysPage(): Promise<unknown> {
    return browser.get('/admin/clinicfreedays') as Promise<unknown>;
  }
  getLabelLoginText(): Promise<string> {
    return element(by.css('app-login label[for=login]')).getText() as Promise<string>;
  }
  loginAsAdmin(login: string, password: string) {
    this.login.sendKeys(login);
    this.password.sendKeys(password);
    this.submitLogin.click();
  }
  getErrorMsg() {
    return this.errorMsg;
  }
  updateAdminData(phone: string, password1: string, password2: string) {
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
  setClinicFreeDayDate(): string {
    const dateFrom = new Date();
    dateFrom.setDate(dateFrom.getDate() + 7);
    const freeDay = dateFrom.toISOString().slice(0, 10);
    this.clinicFreeDay.sendKeys(freeDay);
    this.submitSaveData.click();
    return freeDay;
  }
  selectClinicFreeDayToDelete(clinicFreeDayDate: string){
    element(by.id(clinicFreeDayDate)).click();
  }
  deleteClinicFreeDay(){
    this.submitDeleteFreeDay.click();
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
