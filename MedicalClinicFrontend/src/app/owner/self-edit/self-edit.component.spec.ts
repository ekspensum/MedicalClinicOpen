import { APP_BASE_HREF } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { TranslateFakeLoader, TranslateLoader, TranslateModule, TranslateService } from '@ngx-translate/core';
import { MockAuthenticationService } from 'src/app/mocks/mock-authentication.service';
import { MockUserService } from 'src/app/mocks/mock-user.service';
import { UserService } from 'src/app/services/user.service';
import { SelfEditOwnerComponent } from './self-edit.component';

describe('SelfEditOwnerComponent', () => {
  let component: SelfEditOwnerComponent;
  let fixture: ComponentFixture<SelfEditOwnerComponent>;
  let authService;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [SelfEditOwnerComponent],
      imports: [
        HttpClientModule,
        RouterTestingModule,
        FormsModule,
        TranslateModule.forRoot({
          loader: {
            provide: TranslateLoader,
            useClass: TranslateFakeLoader
          }
        })
      ],
      providers: [
        { provide: APP_BASE_HREF, useValue: '/' },
        { provide: UserService, useClass: MockUserService }
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SelfEditOwnerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    authService = TestBed.inject(MockAuthenticationService);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  it('should be correct admin/employee username from MockUserService', async(() => {
    fixture.whenStable().then(() => {
      const userNameInput: HTMLInputElement = fixture.nativeElement.querySelector('#username');
      expect(component.owner.user.username).toEqual(userNameInput.value);
    });
  }));
  it('should call checkUsernameAndUpdate', () => {
    spyOn(window, 'confirm').and.returnValue(true);
    spyOn(component, 'checkUsernameAndUpdate');
    component.checkUsernameAndUpdate();
    expect(component.checkUsernameAndUpdate).toHaveBeenCalled();
    expect(authService.logOut).toBeDefined();
  });
  it('after call updateOwner should be return UPDATED', () => {
    component.updateOwner();
    expect(component.answer.serverAnswer).toEqual('UPDATED');
  });
  it('should be correct state onCorrectUpdate', () => {
    component.onCorrectUpdate();
    expect(component.answer.serverAnswer).toBeNull();
    expect(component.answer.visableForm).toBeTruthy();
    expect(component.answer.notunique).toBeFalsy;
  });
});
