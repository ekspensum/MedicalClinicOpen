import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HeaderComponent } from './header/header.component';
import { FooterComponent } from './footer/footer.component';
import { MainMenuComponent } from './main-menu/main-menu.component';
import { UserService } from './services/user.service';
import { LanguageService } from './services/language.service';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { HttpClientModule } from '@angular/common/http';
import { AdminComponent } from './admin/admin.component';
import { AddEmployeeComponent } from './admin/add-employee/add-employee.component';
import { EditEmployeeComponent } from './admin/edit-employee/edit-employee.component';
import { HomeComponent } from './home/home.component';
import { AgendaComponent } from './home/agenda/agenda.component';
import { ContactUsComponent } from './home/contact-us/contact-us.component';
import { LoginComponent } from './home/login/login.component';
import { PageNotFoundComponent } from './home/page-not-found/page-not-found.component';
import { AddDoctorComponent } from './admin/add-doctor/add-doctor.component';
import { EditDoctorComponent } from './admin/edit-doctor/edit-doctor.component';
import { EditPatientComponent } from './admin/edit-patient/edit-patient.component';
import { AddPatientComponent } from './admin/add-patient/add-patient.component';
import { RemovePatientComponent } from './admin/remove-patient/remove-patient.component';
import { AuthenticationService } from './services/authentication.service';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { OwnerComponent } from './owner/owner.component';
import { AddAdminComponent } from './owner/add-admin/add-admin.component';
import { EditAdminComponent } from './owner/edit-admin/edit-admin.component';
import { SelfEditOwnerComponent } from './owner/self-edit/self-edit.component';
import { AuthGuardOwnerService } from './services/auth-guard-owner.service';
import { AuthGuardAdminService } from './services/auth-guard-admin.service';
import { RecaptchaFormsModule, RecaptchaModule } from 'ng-recaptcha';
import { HomeService } from './services/home.service';
import { AgmCoreModule } from '@agm/core';
import { ForgetPasswordComponent } from './home/forget-password/forget-password.component';
import { RegisterPatientComponent } from './home/register-patient/register-patient.component';
import { ResetPasswordComponent } from './home/reset-password/reset-password.component';
import { EmployeeComponent } from './employee/employee.component';
import { PatientComponent } from './patient/patient.component';
import { ActivatePatientComponent } from './home/activate-patient/activate-patient.component';
import { AuthGuardEmployeeService } from './services/auth-guard-employee.service';
import { AuthGuardPatientService } from './services/auth-guard-patient.service';
import { SelfEditPatientComponent } from './patient/self-edit-patient/self-edit-patient.component';
import { SelfRemovePatientComponent } from './patient/self-remove-patient/self-remove-patient.component';
import { SelfEditEmployeeComponent } from './employee/self-edit-employee/self-edit-employee.component';
import { DoctorComponent } from './doctor/doctor.component';
import { SelfEditDataDoctorComponent } from './doctor/self-edit-data-doctor/self-edit-data-doctor.component';
import { SelfEditAgendaDoctorComponent } from './doctor/self-edit-agenda-doctor/self-edit-agenda-doctor.component';
import { AuthGuardDoctorService } from './services/auth-guard-doctor.service';
import { SelfEditAdminComponent } from './admin/self-edit-admin/self-edit-admin.component';
import { SocialLoginModule, SocialAuthServiceConfig } from "angularx-social-login";
import { GoogleLoginProvider, FacebookLoginProvider } from "angularx-social-login";
import { RegisterSocialUserComponent } from './home/register-social-user/register-social-user.component';
import { ScheduleVisitComponent } from './patient/schedule-visit/schedule-visit.component';
import { CancelVisitComponent } from './patient/cancel-visit/cancel-visit.component';
import { SetDoctorFreeDaysComponent } from './admin/set-doctor-free-days/set-doctor-free-days.component';
import { SetClinicFreeDaysComponent } from './admin/set-clinic-free-days/set-clinic-free-days.component';
import { EditDoctorAgendaComponent } from './admin/edit-doctor-agenda/edit-doctor-agenda.component';
import { MyVisitsComponent } from './patient/my-visits/my-visits.component';
import { VisitDetailsComponent } from './patient/visit-details/visit-details.component';
import { AddPatientByEmployeeComponent } from './employee/add-patient-by-employee/add-patient-by-employee.component';
import { ScheduleVisitByEmployeeComponent } from './employee/schedule-visit-by-employee/schedule-visit-by-employee.component';
import { PatientVisitsComponent } from './employee/patient-visits/patient-visits.component';
import { CancelVisitByEmployeeComponent } from './employee/cancel-visit-by-employee/cancel-visit-by-employee.component';
import { VisitDetailsForEmployeeComponent } from './employee/visit-details-for-employee/visit-details-for-employee.component';
import { VisitConfirmationComponent } from './employee/visit-confirmation/visit-confirmation.component';
import { DoctorVisitsComponent } from './doctor/doctor-visits/doctor-visits.component';
import { VisitDetailsForDoctorComponent } from './doctor/visit-details-for-doctor/visit-details-for-doctor.component';
import { MakeDiagnosisComponent } from './doctor/make-diagnosis/make-diagnosis.component';
import { PdfService } from './services/pdf.service';
import { MedicalDocumentationComponent } from './patient/medical-documentation/medical-documentation.component';
import { NgxImageCompressService } from 'ngx-image-compress';
import { SelfSetDoctorFreeDaysComponent } from './doctor/self-set-doctor-free-days/self-set-doctor-free-days.component';

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    FooterComponent,
    MainMenuComponent,
    AdminComponent,
    AddEmployeeComponent,
    EditEmployeeComponent,
    HomeComponent,
    AgendaComponent,
    ContactUsComponent,
    LoginComponent,
    PageNotFoundComponent,
    AddDoctorComponent,
    EditDoctorComponent,
    EditPatientComponent,
    AddPatientComponent,
    RemovePatientComponent,
    OwnerComponent,
    AddAdminComponent,
    EditAdminComponent,
    SelfEditOwnerComponent,
    ForgetPasswordComponent,
    RegisterPatientComponent,
    ResetPasswordComponent,
    EmployeeComponent,
    PatientComponent,
    ActivatePatientComponent,
    SelfEditPatientComponent,
    SelfRemovePatientComponent,
    SelfEditEmployeeComponent,
    DoctorComponent,
    SelfEditDataDoctorComponent,
    SelfEditAgendaDoctorComponent,
    SelfEditAdminComponent,
    RegisterSocialUserComponent,
    ScheduleVisitComponent,
    CancelVisitComponent,
    SetDoctorFreeDaysComponent,
    SetClinicFreeDaysComponent,
    EditDoctorAgendaComponent,
    MyVisitsComponent,
    VisitDetailsComponent,
    AddPatientByEmployeeComponent,
    ScheduleVisitByEmployeeComponent,
    PatientVisitsComponent,
    CancelVisitByEmployeeComponent,
    VisitDetailsForEmployeeComponent,
    VisitConfirmationComponent,
    DoctorVisitsComponent,
    VisitDetailsForDoctorComponent,
    MakeDiagnosisComponent,
    MedicalDocumentationComponent,
    SelfSetDoctorFreeDaysComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    TranslateModule.forRoot(),
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    RecaptchaModule,
    RecaptchaFormsModule,
    AgmCoreModule.forRoot({
      apiKey: 'xxxxxxxxxxxxxxxxxxxxxxxxxx'
    }),
    SocialLoginModule
  ],
  providers: [UserService, TranslateService, LanguageService, AuthenticationService, HomeService, PdfService, NgxImageCompressService,
    AuthGuardOwnerService, AuthGuardAdminService, AuthGuardEmployeeService, AuthGuardDoctorService, AuthGuardPatientService,
    {
      provide: 'SocialAuthServiceConfig',
      useValue: {
        autoLogin: false,
        providers: [
          {
            id: GoogleLoginProvider.PROVIDER_ID,
            provider: new GoogleLoginProvider('xxxxxxxxxxxxxxxxxxxxxxxxxxx'),
          },
          {
            id: FacebookLoginProvider.PROVIDER_ID,
            provider: new FacebookLoginProvider('xxxxxxxxxxxxxxxxxxxxxx'),
          }
        ],
      } as SocialAuthServiceConfig,
    }],
  bootstrap: [AppComponent]
})
export class AppModule { }
