import { APP_BASE_HREF } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { TranslateFakeLoader, TranslateLoader, TranslateModule, TranslateService } from '@ngx-translate/core';
import { MockUserService } from 'src/app/mocks/mock-user.service';
import { UserService } from 'src/app/services/user.service';
import { EditDoctorComponent } from './edit-doctor.component';

describe('EditDoctorComponent', () => {
  let component: EditDoctorComponent;
  let fixture: ComponentFixture<EditDoctorComponent>;
  let mockUserService;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [EditDoctorComponent],
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
    fixture = TestBed.createComponent(EditDoctorComponent);
    component = fixture.componentInstance;
    mockUserService = TestBed.inject(MockUserService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  it('shuold be correct state on assignUserData', ()=> {
    component.selectedIndex = 0;
    component.assignUserData();
    expect(component.userEnabled).toBeFalse();
    expect(component.user_photo).toEqual('data:image/png;base64,some image')
  });
  it('doctor list length should be 1', () => {
    expect(component.doctorList.length).toEqual(1);
  });
  it('after call resetDoctorPassword server answer should be equal RESET', () => {
    component.selectedIndex = 0;
    component.resetDoctorPassword();
    expect(component.answer.serverAnswer).toEqual('RESET');
  });
  it('after call updateDoctor should be return UPDATED', ()=> {
    component.selectedIndex = 0;
    component.updateDoctor();
    expect(component.answer.serverAnswer).toEqual('UPDATED');
  });
  it('shuold be correct state onCorrectUpdate', ()=> {
    component.onCorrectUpdate()
    expect(component.answer.serverAnswer).toBeNull();
  });
});
