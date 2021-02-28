import { browser, by, element } from 'protractor';

export class DoctorPage {
  login = element(by.css('input[name=login]'));
  password = element(by.css('input[name=password]'));
  password1 = element(by.css('input[name=password1]'));
  password2 = element(by.css('input[name=password2]'));
  doctorFreeDay = element(by.id('doctorFreeDay'));
  submitLogin = element(by.buttonText('Sign in'));
  submitLogout = element(by.buttonText('Logout'));
  submitSaveData = element(by.buttonText('Save data'));
  submitDeleteFreeDay = element(by.buttonText('Delete'));
  errorMsg = element(by.css('strong'));
  submitUpdate = element(by.buttonText('Save data'));
  streetNo = element(by.css('input[name=streetNo]'));
  confirm = element(by.buttonText('OK'));

  navigateToLoginPage(): Promise<unknown> {
    return browser.get('/login') as Promise<unknown>;
  }
  navigateToSelfEditPage(): Promise<unknown> {
    return browser.get('/doctor/selfeditdata') as Promise<unknown>;
  }
  navigateToDoctorFreeDaysPage(): Promise<unknown> {
    return browser.get('/doctor/selfsetfreedays') as Promise<unknown>;
  }
  getLabelLoginText(): Promise<string> {
    return element(by.css('app-login label[for=login]')).getText() as Promise<string>;
  }
  loginAsDoctor(login: string, password: string) {
    this.login.sendKeys(login);
    this.password.sendKeys(password);
    this.submitLogin.click();
  }
  updateDoctorData(streetNo: string, password1: string, password2: string) {
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
  setDoctorFreeDayDate(): string {
    const dateFrom = new Date();
    dateFrom.setDate(dateFrom.getDate() + 7);
    const freeDay = dateFrom.toISOString().slice(0, 10);
    this.doctorFreeDay.sendKeys(freeDay);
    this.submitSaveData.click();
    return freeDay;
  }
  selectDoctorFreeDayToDelete(doctorFreeDayDate: string){
    element(by.id(doctorFreeDayDate)).click();
  }
  deleteDoctorFreeDay(){
    this.submitDeleteFreeDay.click();
  }
  logout(){
    this.submitLogout.click();
  }
}
