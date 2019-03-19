import { Applicant } from './dashboard.modal';

describe('AttributeModel interface check', () => {
  it('should conform to Attribute interface', () => {
    const attributeModel: Applicant = {
      applicationID: '',
      appointmentDateTime: '',
      name: '',
      nameInSecondaryLanguage: '',
      postalCode: '',
      regDto: '',
      status: ''
    };

    expect(typeof attributeModel.applicationID).toEqual('string');
  });
});
