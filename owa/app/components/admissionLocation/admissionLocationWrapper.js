import React from 'react';
import PropTypes from 'prop-types';
import axios from 'axios';
import _ from 'lodash';

import Header from 'components/header';
import LocationHierarchy from 'components/admissionLocation/leftPanel/locationHierarchy';
import AdmissionLocationList from 'components/admissionLocation/rightPanel/admissionLocationList';
import AddEditAdmissionLocation
    from 'components/admissionLocation/rightPanel/admissionLocationForm/addEditAdmissionLocation';
import SetBedLayout from 'components/admissionLocation/rightPanel/admissionLocationForm/setBedLayout';
import AddEditBed from 'components/admissionLocation/rightPanel/admissionLocationForm/addEditBed';
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
            visitLocations: {},
            bedTypes: [],
            isOpen: true,
            activeUuid: null,
            activePage: 'listing',
            pageData: {},
            config: {}
        };

        this.intl = context.intl;
        this.fetchAllAdmissionLocations = this.fetchAllAdmissionLocations.bind(this);
        this.fetchAllVisitLocations = this.fetchAllVisitLocations.bind(this);
        this.fetchBedTypes = this.fetchBedTypes.bind(this);
        this.fetchConfig = this.fetchConfig.bind(this);
        this.getBody = this.getBody.bind(this);
        this.fetchAllAdmissionLocations(this);
        this.fetchAllVisitLocations(this);
        this.fetchBedTypes();
        this.fetchConfig();
    }

    fetchAllAdmissionLocations(self) {
        axios
            .get(this.urlHelper.apiBaseUrl() + '/location?tag=Admission%20Location&v=full')
            .then(function (admissionLocationsResponse) {
                const admissionLocationUuidList =
                    _.map(admissionLocationsResponse.data.results, (location) => location.uuid);

                const admissionLocations = _.reduce(
                    admissionLocationsResponse.data.results,
                    (acc, curr) => {
                        if (_.includes(admissionLocationUuidList, curr.uuid)) {
                            acc[curr.uuid] = {
                                uuid: curr.uuid,
                                name: curr.name,
                                description: curr.description,
                                parentAdmissionLocationUuid:
                                    curr.parentLocation != null ? curr.parentLocation.uuid : null,
                                isOpen:
                                    typeof self.state.admissionLocations[curr.uuid] != 'undefined'
                                        ? self.state.admissionLocations[curr.uuid].isOpen
                                        : false,
                                isHigherLevel:
                                    curr.parentLocation != null
                                        ? !_.includes(admissionLocationUuidList, curr.parentLocation.uuid)
                                        : true
                            };
                        }

                        return acc;
                    },
                    {}
                );

                self.setState({admissionLocations: admissionLocations});
            })
            .catch(function (errorResponse) {
                const error = errorResponse.response.data ? errorResponse.response.data.error : errorResponse;
                self.admissionLocationFunctions.notify('error', error.message.replace(/\[|\]/g, ''));
            });
    }

    fetchAllVisitLocations(self) {
        axios
            .get(this.urlHelper.apiBaseUrl() + '/location?tag=Visit%20Location&v=full')
            .then(function (visitLocationsResponse) {
                const visitLocationUuidList =
                    _.map(visitLocationsResponse.data.results, (location) => location.uuid);
                const visitLocations = _.reduce(
                    visitLocationsResponse.data.results,
                    (acc, curr) => {
                        if (_.includes(visitLocationUuidList, curr.uuid)) {
                            acc[curr.uuid] = {
                                uuid: curr.uuid,
                                name: curr.name,
                                description: curr.description
                            };
                        }

                        return acc;
                    },
                    {}
                );
                self.setState({visitLocations: visitLocations});
            })
            .catch(function (errorResponse) {
                const error = errorResponse.response.data ? errorResponse.response.data.error : errorResponse;
                self.admissionLocationFunctions.notify('error', error.message.replace(/\[|\]/g, ''));
            });
    }

    fetchBedTypes() {
        const self = this;
        axios
            .get(this.urlHelper.apiBaseUrl() + '/bedtype', {
                params: {
                    v: 'full'
                }
            })
            .then(function (response) {
                self.setState({
                    bedTypes: response.data.results
                });
            })
            .catch(function (errorResponse) {
                const error = errorResponse.response.data ? errorResponse.response.data.error : errorResponse;
                self.admissionLocationFunctions.notify('error', error.message.replace(/\[|\]/g, ''));
            });
    }

    fetchConfig() {
        const self = this;
        const prefix = 'bedmanagement.owa.';
        axios.get(this.urlHelper.apiBaseUrl() + '/systemsetting?v=custom:(property,value)&q=' + prefix)
            .then(function(response) {
                const settings = {};
                if (response.data.results) {
                    response.data.results.forEach(s => {
                        settings[s.property.substring(prefix.length)] = s.value;
                    });
                }
                self.setState({config: settings});
            })
            .catch(function(errorResponse) {
                self.setState({config: {}});
            }
        )
    };

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
        reFetchAllAdmissionLocations: () => {
            this.fetchAllAdmissionLocations(this);
        },
        getAdmissionLocations: () => {
            return this.state.admissionLocations;
        },
        getVisitLocations: () => {
            return this.state.visitLocations;
        },
        getBedTypes: () => {
            return this.state.bedTypes;
        },
        getAdmissionLocationByUuid: (admissionLocationUuid) => {
            return this.admissionLocationHelper.getAdmissionLocation(
                this.state.admissionLocations,
                admissionLocationUuid
            );
        },
        getParentAdmissionLocation: (admissionLocationUuid) => {
            return this.admissionLocationHelper.getParentAdmissionLocation(
                this.state.admissionLocations,
                admissionLocationUuid
            );
        },
        getChildAdmissionLocations: (admissionLocationUuid) => {
            return this.admissionLocationHelper.getChildAdmissionLocations(
                this.state.admissionLocations,
                admissionLocationUuid
            );
        },
        isManagingLocationsEnabled: () => {
            return !(this.state.config.enableManagingLocations === "false");
        },
        notify: (notifyType, message) => {
            const self = this;
            const successText = this.intl.formatMessage({id: 'SUCCESS'});
            const errorText = this.intl.formatMessage({id: 'ERROR'});
            const infoText = this.intl.formatMessage({id: 'INFO'});
            if (notifyType == 'success') {
                self.refs.notificator.success(successText, message, 5000);
            } else if (notifyType == 'error') {
                self.refs.notificator.error(errorText, message, 15000);
            } else {
                self.refs.notificator.error(infoText, message, 5000);
            }
        }
    };

    style = {
        wrapper: {
            marginTop: 10,
            paddingTop: 20,
            borderRadius: 5,
            backgroundColor: '#fff',
            minHeight: 500
        }
    };

    getBody() {
        if (this.state.activePage == 'listing') {
            return (
                <AdmissionLocationList
                    activeUuid={this.state.activeUuid}
                    admissionLocationFunctions={this.admissionLocationFunctions}
                />
            );
        } else if (this.state.activePage == 'addEditLocation') {
            return (
                <AddEditAdmissionLocation
                    operation={this.state.pageData.operation}
                    activeUuid={this.state.activeUuid}
                    admissionLocationFunctions={this.admissionLocationFunctions}
                />
            );
        } else if (this.state.activePage == 'set-layout') {
            return (
                <SetBedLayout
                    activeUuid={this.state.activeUuid}
                    row={this.state.pageData.row}
                    column={this.state.pageData.column}
                    admissionLocationFunctions={this.admissionLocationFunctions}
                />
            );
        } else if (this.state.activePage == 'addEditBed') {
            return (
                <AddEditBed
                    operation={this.state.pageData.operation}
                    layoutColumn={this.state.pageData.layoutColumn}
                    layoutRow={this.state.pageData.layoutRow}
                    bed={this.state.pageData.bed}
                    bedTypes={this.state.bedTypes}
                    activeUuid={this.state.activeUuid}
                    admissionLocationFunctions={this.admissionLocationFunctions}
                />
            );
        }
    }

    render() {
        return (
            <div>
                <ReactNotify ref="notificator"/>
                <Header path={this.props.match.path}/>
                <div style={this.style.wrapper}>
                    <LocationHierarchy
                        admissionLocationFunctions={this.admissionLocationFunctions}
                        isOpen={this.state.isOpen}
                    />
                    {this.getBody()}
                </div>
            </div>
        );
    }
}

AdmissionLocationWrapper.contextTypes = {
    store: PropTypes.object,
    intl: PropTypes.object
};
