import React from 'react';
import PropTypes from 'prop-types';
import axios from 'axios';
import _ from 'lodash';

import Header from 'components/header';
import LocationHierarchy from 'components/admissionLocation/leftPanel/locationHierarchy';
import AdmissionLocationList from 'components/admissionLocation/rightPanel/admissionLocationList';
import AdmissionLocationHelper from 'utilities/admissionLocationHelper';
import UrlHelper from 'utilities/urlHelper';
import ReactNotify from 'react-notify';

export default class AdmissionLocationWrapper extends React.Component {
    constructor(props, context) {
        super(props, context);

        this.admissionLocationHelper = new AdmissionLocationHelper();
        this.urlHelper = new UrlHelper();
        this.state = {
            admissionLocations: {},
            visitLocations : {},
            bedTypes: [],
            isOpen: true,
            activeUuid: null,
            activePage: 'listing',
            pageData:{}
        };

        this.fetchAllAdmissionLocations = this.fetchAllAdmissionLocations.bind(this);
        this.fetchBedTypes = this.fetchBedTypes.bind(this);
        this.getPage = this.getPage.bind(this);
        this.fetchAllAdmissionLocations(this);
        this.fetchBedTypes();
    }

    fetchAllAdmissionLocations(self) {
        axios.get(this.urlHelper.apiBaseUrl() + '/location', {
            params: {
                v: 'full'
            }
        }).then(function (response) {
            const admissionLocationUuidList = [];
            const visitLocationUuidList = [];
            _.forEach(response.data.results, (location) => {
                _.each(location.tags, (tag) => {
                    if(tag.display == 'Admission Location'){
                        admissionLocationUuidList.push(location.uuid);
                    } else if(tag.display == 'Visit Location'){
                        visitLocationUuidList.push(location.uuid);
                    }
                });
            });

            const admissionLocations = _.reduce(response.data.results, (acc, curr) => {
                if(_.includes(admissionLocationUuidList, curr.uuid)){
                    acc[curr.uuid] = {
                        uuid: curr.uuid,
                        name: curr.name,
                        description: curr.description,
                        parentAdmissionLocationUuid: curr.parentLocation != null ? curr.parentLocation.uuid : null,
                        isOpen: typeof self.state.admissionLocations[curr.uuid] != 'undefined' ? self.state.admissionLocations[curr.uuid].isOpen : false,
                        isHigherLevel: curr.parentLocation != null ? !_.includes(admissionLocationUuidList, curr.parentLocation.uuid) : true
                    };
                }

                return acc;
            }, {});

            const visitLocations = _.reduce(response.data.results, (acc, curr) => {
                if(_.includes(visitLocationUuidList, curr.uuid)){
                    acc[curr.uuid] = {
                        uuid: curr.uuid,
                        name: curr.name,
                        description: curr.description
                    };
                }

                return acc;
            }, {});

            self.setState({
                admissionLocations: admissionLocations,
                visitLocations : visitLocations
            });
        }).catch(function (error) {
            self.props.admissionLocationFunctions.notify('error', error.message);
        });
    }

    fetchBedTypes() {
        const self = this;
        axios.get(this.urlHelper.apiBaseUrl() + '/bedtype', {
            params: {
                v: 'full'
            }
        }).then(function (response) {
            self.setState({
                bedTypes: response.data.results
            });
        }).catch(function (error) {
            self.props.admissionLocationFunctions.notify('error', error.message);
        });
    }

    admissionLocationFunctions = {
        setActiveLocationUuid: (admissionLocationUuid) => {
            this.setState({
                activeUuid: admissionLocationUuid
            });
        },
        getActiveLocationUuid: () => {
            return this.state.activeUuid;
        },
        setState: (data) => {
            this.setState({
                ...data
            });
        },
        getState: () => {
            return this.state;
        },
        reFetchAllAdmissionLocations : () => {
            this.fetchAllAdmissionLocations(this);
        },
        getAdmissionLocations: () => {
            return this.state.admissionLocations;
        },
        getVisitLocations: () => {
            return this.state.visitLocations;
        },
        getAdmissionLocationByUuid: (admissionLocationUuid) => {
            return this.admissionLocationHelper.getAdmissionLocation(this.state.admissionLocations, admissionLocationUuid);
        },
        getParentAdmissionLocation: (admissionLocationUuid) => {
            return this.admissionLocationHelper.getParentAdmissionLocation(this.state.admissionLocations, admissionLocationUuid);
        },
        getChildAdmissionLocations : (admissionLocationUuid) => {
            return this.admissionLocationHelper.getChildAdmissionLocations(this.state.admissionLocations, admissionLocationUuid);
        },
        notify: (notifyType, message) => {
            const self = this;
            if (notifyType == 'success') {
                self.refs.notificator.success('Success', message, 5000);
            } else if (notifyType == 'error') {
                self.refs.notificator.error('Error', message, 5000);
            } else {
                self.refs.notificator.error('Info', message, 5000);
            }
        }
    };

    style = {
        container: {
            height: '100%'
        },
        wrapper: {
            marginTop: 30,
            height: '100%'
        }
    };

    getPage() {
        return <AdmissionLocationList activeUuid={this.state.activeUuid}
            admissionLocationFunctions={this.admissionLocationFunctions}/>;
    }

    render() {
        return <div style={this.style.container}>
            <Header path={this.props.match.path}/>
            <div style={this.style.wrapper}>
                <LocationHierarchy admissionLocationFunctions={this.admissionLocationFunctions}
                    isOpen={this.state.isOpen} />
                {this.getPage()}
            </div>
        </div>;
    }
}

AdmissionLocationWrapper.contextTypes = {
    store: PropTypes.object
};