import { OwnerPage } from './owner.po';
import { browser, logging } from 'protractor';
import { protractor } from 'protractor/built/ptor';

describe('workspace-project Owner', () => {
  let page: OwnerPage;


  beforeEach(() => {
    page = new OwnerPage();
  });

  it('should go to login page, display label for login, login as owner, go to self edit page, update own data and logout', () => {
    page.navigateToLoginPage();
    expect(page.getLabelLoginText()).toEqual('Login/email:');
    page.loginAsOwner('xxxxxxxxx', 'xxxxxxxxxxx');
    const EC = protractor.ExpectedConditions;
    const urlOwnerChanged = EC.urlContains('/owner')
    browser.wait(urlOwnerChanged, 5000, 'url should contain /owner');
    page.navigateToSelfEditPage();
    const urlSelfEditChanged = EC.urlContains('/owner/selfedit')
    browser.wait(urlSelfEditChanged, 5000, 'url should contain /owner/selfedit');
    const randomStreetNo = Math.round(Math.random() * 1000000);
    page.updateOwnerData(randomStreetNo.toString(), 'xxxxxxxxxxxx', 'xxxxxxxxxxxxxx');
    browser.sleep(1000);
    page.confirmAlert();
    expect(page.getUpdatedStreetNo()).toEqual(randomStreetNo.toString());
    page.logout();
    const url = browser.getCurrentUrl();
    expect(url).toContain('/login');
  });

});
