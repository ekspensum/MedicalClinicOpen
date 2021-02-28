import { browser } from 'protractor';
import { protractor } from 'protractor/built/ptor';
import { PatientPage } from './patient.po';

describe('workspace-project Patient', () => {
  let page: PatientPage;


  beforeEach(() => {
    page = new PatientPage();
  });

  it('should go to login page, display label for login, login as patient, go to self edit page, update own data and logout', () => {
    page.navigateToLoginPage();
    expect(page.getLabelLoginText()).toEqual('Login/email:');
    page.loginAsPatient('xxxxxxxxxxx', 'xxxxxxxxxxxxxxxxx');
    const EC = protractor.ExpectedConditions;
    const urlPatientChanged = EC.urlContains('/patient')
    browser.wait(urlPatientChanged, 5000, 'url should contain /patient');
    page.navigateToSelfEditPage();
    const urlSelfEditChanged = EC.urlContains('/patient/selfedit')
    browser.wait(urlSelfEditChanged, 5000, 'url should contain /patient/selfedit');
    const randomPhone = Math.round(Math.random() * 1000000000);
    page.updatePatientData(randomPhone.toString(), 'xxxxxxxxxxxxxx', 'xxxxxxxxxxxxxxxxx');
    browser.sleep(1000);
    page.confirmAlert();
    expect(page.getUpdatedStreetNo()).toEqual(randomPhone.toString());
    page.logout();
    const url = browser.getCurrentUrl();
    expect(url).toContain('/login');
  });

});
