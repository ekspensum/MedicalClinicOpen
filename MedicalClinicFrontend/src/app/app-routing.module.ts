import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AdminComponent } from './admin/admin.component';
import { AddEmployeeComponent } from './admin/add-employee/add-employee.component';
import { EditEmployeeComponent } from './admin/edit-employee/edit-employee.component';
import { PageNotFoundComponent } from './home/page-not-found/page-not-found.component';
import { LoginComponent } from './home/login/login.component';
import { ContactUsComponent } from './home/contact-us/contact-us.component';
import { AgendaComponent } from './home/agenda/agenda.component';
import { HomeComponent } from './home/home.component';
import { AddDoctorComponent } from './admin/add-doctor/add-doctor.component';
import { EditDoctorComponent } from './admin/edit-doctor/edit-doctor.component';
import { AddPatientComponent } from './admin/add-patient/add-patient.component';
import { EditPatientComponent } from './admin/edit-patient/edit-patient.component';
import { RemovePatientComponent } from './admin/remove-patient/remove-patient.component';
import { AuthGuardAdminService } from './services/auth-guard-admin.service';
import { OwnerComponent } from './owner/owner.component';
import { AddAdminComponent } from './owner/add-admin/add-admin.component';
import { EditAdminComponent } from './owner/edit-admin/edit-admin.component';
import { AuthGuardOwnerService } from './services/auth-guard-owner.service';
import { SelfEditOwnerComponent } from './owner/self-edit/self-edit.component';
import { ForgetPasswordComponent } from './home/forget-password/forget-password.component';
import { RegisterPatientComponent } from './home/register-patient/register-patient.component';
import { ResetPasswordComponent } from './home/reset-password/reset-password.component';
import { EmployeeComponent } from './employee/employee.component';
import { AuthGuardEmployeeService } from './services/auth-guard-employee.service';
import { PatientComponent } from './patient/patient.component';
import { AuthGuardPatientService } from './services/auth-guard-patient.service';
import { ActivatePatientComponent } from './home/activate-patient/activate-patient.component';
import { SelfEditPatientComponent } from './patient/self-edit-patient/self-edit-patient.component';
import { SelfRemovePatientComponent } from './patient/self-remove-patient/self-remove-patient.component';
import { SelfEditEmployeeComponent } from './employee/self-edit-employee/self-edit-employee.component';
import { DoctorComponent } from './doctor/doctor.component';
import { SelfEditDataDoctorComponent } from './doctor/self-edit-data-doctor/self-edit-data-doctor.component';
import { SelfEditAgendaDoctorComponent } from './doctor/self-edit-agenda-doctor/self-edit-agenda-doctor.component';
import { AuthGuardDoctorService } from './services/auth-guard-doctor.service';
import { SelfEditAdminComponent } from './admin/self-edit-admin/self-edit-admin.component';
import { RegisterSocialUserComponent } from './home/register-social-user/register-social-user.component';
import { AuthGuardTemporaryUserService } from './services/auth-guard-temporary-user.service';
import { ScheduleVisitComponent } from './patient/schedule-visit/schedule-visit.component';
import { CancelVisitComponent } from './patient/cancel-visit/cancel-visit.component';
import { SetDoctorFreeDaysComponent } from './admin/set-doctor-free-days/set-doctor-free-days.component';
import { SetClinicFreeDaysComponent } from './admin/set-clinic-free-days/set-clinic-free-days.component';
import { EditDoctorAgendaComponent } from './admin/edit-doctor-agenda/edit-doctor-agenda.component';
import { MyVisitsComponent } from 'src/app/patient/my-visits/my-visits.component';
import { VisitDetailsComponent } from 'src/app/patient/visit-details/visit-details.component';
import { AddPatientByEmployeeComponent } from 'src/app/employee/add-patient-by-employee/add-patient-by-employee.component';
import { ScheduleVisitByEmployeeComponent } from './employee/schedule-visit-by-employee/schedule-visit-by-employee.component';
import { PatientVisitsComponent } from './employee/patient-visits/patient-visits.component';
import { CancelVisitByEmployeeComponent } from './employee/cancel-visit-by-employee/cancel-visit-by-employee.component';
import { VisitDetailsForEmployeeComponent } from './employee/visit-details-for-employee/visit-details-for-employee.component';
import { VisitConfirmationComponent } from './employee/visit-confirmation/visit-confirmation.component';
import { DoctorVisitsComponent } from './doctor/doctor-visits/doctor-visits.component';
import { VisitDetailsForDoctorComponent } from './doctor/visit-details-for-doctor/visit-details-for-doctor.component';
import { MakeDiagnosisComponent } from './doctor/make-diagnosis/make-diagnosis.component';
import { MedicalDocumentationComponent } from './patient/medical-documentation/medical-documentation.component';
import { SelfSetDoctorFreeDaysComponent } from './doctor/self-set-doctor-free-days/self-set-doctor-free-days.component';

export const routes: Routes = [
  {
    path: 'owner', component: OwnerComponent, children: [
      { path: 'addadmin', component: AddAdminComponent },
      { path: 'editadmin', component: EditAdminComponent },
      { path: 'selfedit', component: SelfEditOwnerComponent }
    ], canActivate: [AuthGuardOwnerService]
  },
  {
    path: 'admin', component: AdminComponent, children: [
      { path: 'addemployee', component: AddEmployeeComponent },
      { path: 'editemployee', component: EditEmployeeComponent },
      { path: 'adddoctor', component: AddDoctorComponent },
      { path: 'editdoctor', component: EditDoctorComponent },
      { path: 'editdoctoragenda', component: EditDoctorAgendaComponent },
      { path: 'addpatient', component: AddPatientComponent },
      { path: 'editpatient', component: EditPatientComponent },
      { path: 'removepatient', component: RemovePatientComponent },
      { path: 'doctorfreedays', component: SetDoctorFreeDaysComponent },
      { path: 'clinicfreedays', component: SetClinicFreeDaysComponent },
      { path: 'selfedit', component: SelfEditAdminComponent }
    ], canActivate: [AuthGuardAdminService]
  },
  {
    path: 'employee', component: EmployeeComponent, children: [
      { path: 'schedulevisit', component: ScheduleVisitByEmployeeComponent },
      { path: 'patientvisits', component: PatientVisitsComponent },
      { path: 'visitdetails', component: VisitDetailsForEmployeeComponent },
      { path: 'visitconfirmation', component: VisitConfirmationComponent },
      { path: 'cancelvisit', component: CancelVisitByEmployeeComponent },
      { path: 'addpatient', component: AddPatientByEmployeeComponent },
      { path: 'selfedit', component: SelfEditEmployeeComponent }
    ], canActivate: [AuthGuardEmployeeService]
  },
  {
    path: 'doctor', component: DoctorComponent, children: [
      { path: 'myvisits', component: DoctorVisitsComponent },
      { path: 'visitdetails', component: VisitDetailsForDoctorComponent },
      { path: 'makediagnosis', component: MakeDiagnosisComponent },
      { path: 'selfeditdata', component: SelfEditDataDoctorComponent },
      { path: 'selfeditagenda', component: SelfEditAgendaDoctorComponent },
      { path: 'selfsetfreedays', component: SelfSetDoctorFreeDaysComponent }
    ], canActivate: [AuthGuardDoctorService]
  },
  {
    path: 'patient', component: PatientComponent, children: [
      { path: 'schedulevisit', component: ScheduleVisitComponent },
      { path: 'myvisits', component: MyVisitsComponent },
      { path: 'visitdetails', component: VisitDetailsComponent },
      { path: 'cancelvisit', component: CancelVisitComponent },
      { path: 'medicaldocumentation', component: MedicalDocumentationComponent },
      { path: 'selfedit', component: SelfEditPatientComponent },
      { path: 'removemyaccount', component: SelfRemovePatientComponent }
    ], canActivate: [AuthGuardPatientService]
  },
  { path: '', pathMatch: 'full', component: HomeComponent },
  { path: 'agenda', component: AgendaComponent },
  { path: 'contactus', component: ContactUsComponent },
  { path: 'login', component: LoginComponent },
  { path: 'recoverpassword', component: ForgetPasswordComponent },
  { path: 'registerpatient', component: RegisterPatientComponent },
  { path: 'registersocialuser', component: RegisterSocialUserComponent, canActivate: [AuthGuardTemporaryUserService] },
  { path: 'resetpassword', component: ResetPasswordComponent },
  { path: 'activation', component: ActivatePatientComponent },
  { path: '**', component: PageNotFoundComponent } // this must be on end array
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
