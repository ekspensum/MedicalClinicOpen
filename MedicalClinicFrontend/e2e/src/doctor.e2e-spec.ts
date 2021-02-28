import { DoctorPage } from './doctor.po';
import { browser, logging } from 'protractor';
import { protractor } from 'protractor/built/ptor';

describe('workspace-project Doctor', () => {
  let page: DoctorPage;


  beforeEach(() => {
    page = new DoctorPage();
  });

  it('should go to login page, display label for login, login as doctor, go to self edit page, update own data', () => {
    page.navigateToLoginPage();
    expect(page.getLabelLoginText()).toEqual('Login/email:');
    page.loginAsDoctor('xxxxxxxx', 'xxxxxxxxxxxx');
    const EC = protractor.ExpectedConditions;
    const urlDoctorChanged = EC.urlContains('/doctor')
    browser.wait(urlDoctorChanged, 5000, 'url should contain /doctor');
    page.navigateToSelfEditPage();
    const urlSelfEditChanged = EC.urlContains('/doctor/selfeditdata')
    browser.wait(urlSelfEditChanged, 5000, 'url should contain /doctor/selfeditdata');
    const randomStreetNo = Math.round(Math.random() * 1000000);
    page.updateDoctorData(randomStreetNo.toString(), 'xxxxxxxxxx', 'xxxxxxxxxx');
    browser.sleep(1000);
    page.confirmAlert();
    expect(page.getUpdatedStreetNo()).toEqual(randomStreetNo.toString());
  });
  it('should go to doctor free days page, set new doctor free day, delete it and logout', () => {
    const EC = protractor.ExpectedConditions;
    page.navigateToDoctorFreeDaysPage();
    const urlClinicFreeDaysChanged = EC.urlContains('/doctor/selfsetfreedays')
    browser.wait(urlClinicFreeDaysChanged, 5000, 'url should contain /doctor/selfsetfreedays');
    const freeDay = page.setDoctorFreeDayDate();
    page.selectDoctorFreeDayToDelete(freeDay);
    page.deleteDoctorFreeDay();
    page.confirmAlert();
    page.logout();
    const url = browser.getCurrentUrl();
    expect(url).toContain('/login');
  });

});
