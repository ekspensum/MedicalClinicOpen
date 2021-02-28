import { browser } from 'protractor';
import { protractor } from 'protractor/built/ptor';
import { AdminPage } from './admin.po';

describe('workspace-project Admin', () => {
  let page: AdminPage;


  beforeEach(() => {
    page = new AdminPage();
  });

  it('should stay on login page if password is incorrect', () => {
    page.navigateToLoginPage();
    page.loginAsAdmin('xxxxxxxxx', 'xxxxxxxxxxx');
    const url = browser.getCurrentUrl();
    expect(url).toContain('/login');
    expect(page.getErrorMsg().isPresent()).toBeTruthy();
    page.confirmAlert();
  });
  it('should go to login page, display label for login, login as admin, go to self edit page, update own data', () => {
    page.navigateToLoginPage();
    expect(page.getLabelLoginText()).toEqual('Login/email:');
    page.loginAsAdmin('xxxxxxxxxx', 'xxxxxxxxxxx');
    const EC = protractor.ExpectedConditions;
    const urlAdminChanged = EC.urlContains('/employee')
    browser.wait(urlAdminChanged, 5000, 'url should contain /employee');
    page.navigateToSelfEditPage();
    const urlSelfEditChanged = EC.urlContains('/admin/selfedit')
    browser.wait(urlSelfEditChanged, 5000, 'url should contain /admin/selfedit');
    const randomPhone = Math.round(Math.random() * 1000000000);
    page.updateAdminData(randomPhone.toString(), 'xxxxxxxxxx', 'xxxxxxxxxxxx');
    browser.sleep(1000);
    page.confirmAlert();
    expect(page.getUpdatedStreetNo()).toEqual(randomPhone.toString());
  });
  it('should go to clinic free days page, set new clinic free day, delete it and logout', () => {
    const EC = protractor.ExpectedConditions;
    page.navigateToClinicFreeDaysPage();
    const urlClinicFreeDaysChanged = EC.urlContains('/admin/clinicfreedays')
    browser.wait(urlClinicFreeDaysChanged, 5000, 'url should contain /admin/clinicfreedays');
    const freeDay = page.setClinicFreeDayDate();
    page.selectClinicFreeDayToDelete(freeDay);
    page.deleteClinicFreeDay();
    page.confirmAlert();
    page.logout();
    const url = browser.getCurrentUrl();
    expect(url).toContain('/login');
  });

});
