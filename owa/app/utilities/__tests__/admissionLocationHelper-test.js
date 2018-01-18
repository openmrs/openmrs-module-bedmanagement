import AdmissionLocationHelper from 'utilities/admissionLocationHelper';
import {admissionLocations} from '../../utilities/__tests__/testData.json';

const admissionLocationHelper = new AdmissionLocationHelper();
describe('AdmissionLocationHelper', () => {
    it('Should return admission location by Uuid', () => {
        expect(
            admissionLocationHelper.getAdmissionLocation(admissionLocations, 'bb0e512e-d225-11e4-9c67-080027b662ec')
        ).toEqual({
            name: 'Labour Ward',
            uuid: 'bb0e512e-d225-11e4-9c67-080027b662ec',
            description: 'Hospital Labour Ward Section',
            parentAdmissionLocationUuid: null,
            isOpen: false,
            isHigherLevel: true
        });

        expect(
            admissionLocationHelper.getAdmissionLocation(admissionLocations, 'baf83667-d225-11e4-9c67-080027b662ec')
        ).toEqual({
            name: 'General Ward - floor-2',
            uuid: 'baf83667-d225-11e4-9c67-080027b662ec',
            description: 'General Ward of second floor',
            parentAdmissionLocationUuid: 'baf7bd38-d225-11e4-9c67-080027b662ec',
            isOpen: false,
            isHigherLevel: false
        });
    });

    it('Should return higher level admission locations', () => {
        expect(admissionLocationHelper.getHigherLevelAdmissionLocations(admissionLocations)).toEqual({
            'baf7bd38-d225-11e4-9c67-080027b662ec': {
                name: 'General Ward',
                uuid: 'baf7bd38-d225-11e4-9c67-080027b662ec',
                description: 'Hospital General Ward Section',
                parentAdmissionLocationUuid: 'c1e42932-3f10-11e4-adec-0800271c1b75',
                isOpen: false,
                isHigherLevel: true
            },
            'bb0e512e-d225-11e4-9c67-080027b662ec': {
                name: 'Labour Ward',
                uuid: 'bb0e512e-d225-11e4-9c67-080027b662ec',
                description: 'Hospital Labour Ward Section',
                parentAdmissionLocationUuid: null,
                isOpen: false,
                isHigherLevel: true
            }
        });
    });

    it('Should return child admission locations by parent location uuid', () => {
        expect(
            admissionLocationHelper.getChildAdmissionLocations(
                admissionLocations,
                'baf7bd38-d225-11e4-9c67-080027b662ec'
            )
        ).toEqual({
            'baf83667-d225-11e4-9c67-080027b662ec': {
                isHigherLevel: false,
                isOpen: false,
                name: 'General Ward - floor-2',
                description: 'General Ward of second floor',
                parentAdmissionLocationUuid: 'baf7bd38-d225-11e4-9c67-080027b662ec',
                uuid: 'baf83667-d225-11e4-9c67-080027b662ec'
            },
            'e48fb2b3-d490-11e5-b193-0800270d80ce': {
                isHigherLevel: false,
                isOpen: false,
                name: 'General Ward - Room 2',
                description: 'General Ward room number 2',
                parentAdmissionLocationUuid: 'baf7bd38-d225-11e4-9c67-080027b662ec',
                uuid: 'e48fb2b3-d490-11e5-b193-0800270d80ce'
            }
        });

        expect(admissionLocationHelper.getChildAdmissionLocations(admissionLocations, null)).toEqual({
            'baf7bd38-d225-11e4-9c67-080027b662ec': {
                isHigherLevel: true,
                isOpen: false,
                name: 'General Ward',
                description: 'Hospital General Ward Section',
                parentAdmissionLocationUuid: 'c1e42932-3f10-11e4-adec-0800271c1b75',
                uuid: 'baf7bd38-d225-11e4-9c67-080027b662ec'
            },
            'bb0e512e-d225-11e4-9c67-080027b662ec': {
                isHigherLevel: true,
                isOpen: false,
                name: 'Labour Ward',
                description: 'Hospital Labour Ward Section',
                parentAdmissionLocationUuid: null,
                uuid: 'bb0e512e-d225-11e4-9c67-080027b662ec'
            }
        });

        expect(
            admissionLocationHelper.getChildAdmissionLocations(
                admissionLocations,
                'bb0e512e-d225-11e4-9c67-080027b662ec'
            )
        ).toEqual({});
    });

    it('Should return parents location upto higher level', () => {
        expect(admissionLocationHelper.navigateUpToHigherLevel(admissionLocations, [], null)).toEqual([]);

        expect(
            admissionLocationHelper.navigateUpToHigherLevel(
                admissionLocations,
                [],
                'bb0e512e-d225-11e4-9c67-080027b662ec'
            )
        ).toEqual([
            {
                isHigherLevel: true,
                isOpen: false,
                name: 'Labour Ward',
                description: 'Hospital Labour Ward Section',
                parentAdmissionLocationUuid: null,
                uuid: 'bb0e512e-d225-11e4-9c67-080027b662ec'
            }
        ]);

        expect(
            admissionLocationHelper.navigateUpToHigherLevel(
                admissionLocations,
                [],
                'baf83667-d225-11e4-as58-080027b662ec'
            )
        ).toEqual([
            {
                isHigherLevel: true,
                isOpen: false,
                name: 'General Ward',
                description: 'Hospital General Ward Section',
                parentAdmissionLocationUuid: 'c1e42932-3f10-11e4-adec-0800271c1b75',
                uuid: 'baf7bd38-d225-11e4-9c67-080027b662ec'
            },
            {
                isHigherLevel: false,
                isOpen: false,
                name: 'General Ward - floor-2',
                description: 'General Ward of second floor',
                parentAdmissionLocationUuid: 'baf7bd38-d225-11e4-9c67-080027b662ec',
                uuid: 'baf83667-d225-11e4-9c67-080027b662ec'
            },
            {
                isHigherLevel: false,
                isOpen: false,
                name: 'General Ward - Room-200',
                description: 'General Ward room number 200',
                parentAdmissionLocationUuid: 'baf83667-d225-11e4-9c67-080027b662ec',
                uuid: 'baf83667-d225-11e4-as58-080027b662ec'
            }
        ]);
    });

    it('Should return parent admission location', () => {
        expect(
            admissionLocationHelper.getParentAdmissionLocation(
                admissionLocations,
                'baf83667-d225-11e4-as58-080027b662ec'
            )
        ).toEqual({
            isHigherLevel: false,
            isOpen: false,
            name: 'General Ward - floor-2',
            description: 'General Ward of second floor',
            parentAdmissionLocationUuid: 'baf7bd38-d225-11e4-9c67-080027b662ec',
            uuid: 'baf83667-d225-11e4-9c67-080027b662ec'
        });

        expect(
            admissionLocationHelper.getParentAdmissionLocation(
                admissionLocations,
                'bb0e512e-d225-11e4-9c67-080027b662ec'
            )
        ).toEqual(null);
    });
});
