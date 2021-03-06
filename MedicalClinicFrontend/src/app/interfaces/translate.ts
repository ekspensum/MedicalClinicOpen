export interface Translate {
  ['user']: {
    roleSelect: string;
    roleEmployee: string;
    roleAdmin: string;
    firstRole: string;
    secondRole: string;
    enabled: string;
    firstName: string;
    lastName: string;
    phone: string;
    language: string;
    image: string;
    successSubject: string;
    success_add_msg: string;
    successUpdate: string;
    companyName: string;
    zipCode: string;
    city: string;
    street: string;
    streetNo: string;
    unitNo: string;
    latitude: string;
    longitude: string;
    gender: string;
    male: string;
    female: string;
    country: string;
    registerDateTime: string;
    editDateTime: string;
    searchPatient: string;
    confirmRemove: string;
    successRemove: string;
    removePatientAccount: string;
    removePatientInfo: string;
    cancelRemovePatientAccount: string;
    cancelRemovePatientInfo: string;
    doctorExperience: string;
    orderRemovePatient: string;
    cancelOrderRemovePatient: string
    greeting: string;
    registerSocialUser: string;
    workingHour: string;
    doesNotWork: string;
  };
  ['button']: {
    select: string;
    save: string;
    update: string;
    delete: string;
    search: string;
    removePatient: string;
    register: string;
    schedule: string;
    nextWeek: string;
    previousWeek: string;
    details: string;
    confirm: string;
    diagnosis: string;
    addReferral: string;
    createPdf: string;
    deletePhoto: string;
  };
  ['login']: {
    password: string;
    passwordRepet: string;
    resetPassword: string;
    success_reset_pass: string;
    signin: string;
    recover: string;
    recoverPassPlaceholder: string;
    recoverPassDescription: string;
    recoverPassSentEmail: string;
    resetPasswordPage: string;
    logoutDifferentUsername: string;
    google: string;
    facebook: string;
    register: string;
  };
  ['menu']: {
    owner: string;
    admin: string;
    doctor: string;
    employee: string;
    patient: string;
    home: string;
    agenda: string;
    contactus: string;
    signin: string;
    logged: string;
    logout: string;
    addAdmin: string;
    updateAdmin: string;
    selfUpdate: string;
    removeMyAccount: string;
    addEmployee: string;
    updateEmployee: string;
    addDoctor: string;
    updateDoctor: string;
    addPatient: string;
    updatePatient: string;
    removePatient: string;
    selfEditAgenda: string;
    scheduleVisit: string;
    cancelVisit: string;
    doctorFreeDays: string;
    clinicFreeDays: string;
    updateDoctorAgenda: string;
    myVisits: string;
    patientVisits: string;
    visitConfirmation: string;
    myPlannedVisits: string;
    makeDiagnosis: string;
    medicalDocumentation: string;
    selfSetFreeDays: string;
  };
  ['header']: {
    greeting: string;
    doctorPhone: string;
    patientPhone: string;
    patientAilments: string;
    visitStatus: string;
    patientPhoto: string;
    referrals: string;
    recognition: string;
    researchUnit: string;
    scopeOfExam: string;
    disgnosisAndOrders: string;
    referralToExam: string;
    address: string;
    visitDate: string;
    visitTime: string;
    oncomingVisitList: string;
    visitDateRange: string;
    photo: string;
    description: string;
    enabled: string;
    medicalDocNotFound: string;
    medicalDocumentation: string;
  };
  ['home']: {
    description: string;
    msgSubject: string;
    msgContent: string;
    emailReply: string;
    selectFiles: string;
    sendEmail: string;
    confirmSentMail: string;
    reCaptcha: string;
    fileName: string;
    fileType: string;
    fileSize: string;
    filesClear: string;
    activationPatientPage: string;
    successActivationPatient: string;
    monday: string;
    tuesday: string;
    wednesday: string;
    thursday: string;
    friday: string;
    saturday: string;
    sunday: string;
  };
  ['visit']: {
    scheduled: string;
    addClinicFreeDay: string;
    addDoctorFreeDay: string
    freeDay: string;
    freeDayRemoved: string;
    agenda: string;
    freeTerm: string;
    takenTerm: string;
    selectDoctor: string;
    visitDateTime: string;
    ailmentDiagnosedDoctor: string;
    patientAilmentsInfo: string;
    diagnosis: string;
    deleted: string;
    details: string;
    reservation: string;
    selectedPatient: string;
    visitStatus: string;
    reservationByEmployee: string;
    reservationByPatient: string;
    confirmed: string;
    orders: string;
    medicines: string;
    makedDiagnosis: string;
    updateMedicalDocum: string;
    statusPlanned: string;
    statusConfirmed: string;
    statusCompleted: string;
  };
  ['error']: {
    requiredField: string;
    oryginalPhotoSize: string;
    compressedPhotoSize: string;
    language: string;
    subject: string;
    not_unique: string;
    invalid: string;
    no_connect: string;
    err_400: string;
    err_401: string;
    err_403: string;
    err_404: string;
    err_423: string;
    err_500: string;
    theSameRole: string;
    none: string;
    differentPasswords: string;
    exceedFileNumber: string;
    exceedAllFilesSize: string;
    recoverPassUserUnknown: string;
    recoverPassDeleyed: string;
    gender: string;
    activationPatient: string;
    orderRemovePatient: string;
    orderRemovePatientExist: string;
    cancelOrderRemovePatient: string;
    cancelOrderRemovePatientMail: string;
    orderRemovePatientNotExist: string;
    reCaptcha: string;
    patientNotEnabled: string;
    patientAccountExist: string;
    patientNotEnabledAdmin: string;
    patientAccountExistAdmin: string;
    visitTermTaken: string;
    visitDateTimeEmpty: string;
    pastDate: string;
    notFoundVisit: string;
  };
}
