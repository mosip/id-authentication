import { Applicant } from './dashboard.modal';

describe('AttributeModel interface check', () => {
  it('should conform to Attribute interface', () => {
    const attributeModel: Applicant = {
      applicationID: '',
      appointmentDate: '',
      appointmentDateTime: '',
      appointmentTime: '',
      name: '',
      nameInSecondaryLanguage: '',
      postalCode: '',
      regDto: '',
      status: ''
    };

    expect(typeof attributeModel.applicationID).toEqual('string');
  });
});
