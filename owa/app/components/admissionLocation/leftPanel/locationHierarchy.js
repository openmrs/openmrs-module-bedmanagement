import React from 'react';
import PropTypes from 'prop-types';

import AdmissionLocationHelper from 'utilities/admissionLocationHelper';
import HierarchyItem from 'components/admissionLocation/leftPanel/hierarchyItem';

require('./hierarchy.css');
export default class LocationHierarchy extends React.Component {
    constructor(props, context) {
        super(props, context);

        this.intl = context.intl;
        this.admissionLocationHelper = new AdmissionLocationHelper();
        this.higherLevelAdmissionLocations = this.admissionLocationHelper.getHigherLevelAdmissionLocations(
            props.admissionLocationFunctions.getAdmissionLocations()
        );
        this.onClickTitle = this.onClickTitle.bind(this);
        this.onClickIcon = this.onClickIcon.bind(this);
    }

    hierarchyFunction = {
        toggleIsOpen: (locationUuid, isOpen) => {
            if (locationUuid != null) {
                this.hierarchyFunction.setAdmissionLocationIsOpen(locationUuid, !isOpen);
            } else {
                this.props.admissionLocationFunctions.setState({
                    isOpen: this.props.isOpen ? false : true
                });
            }
        },
        setAdmissionLocationIsOpen: (locationUuid, isOpen) => {
            const admissionLocations = this.hierarchyFunction.getAdmissionLocations();
            if (admissionLocations[locationUuid].isOpen !== isOpen) {
                admissionLocations[locationUuid].isOpen = isOpen;
                this.props.admissionLocationFunctions.setState({
                    admissionLocations: admissionLocations
                });
            }
        },
        setState: (stateData) => {
            this.props.admissionLocationFunctions.setState(stateData);
        },
        getActiveUuid: () => {
            return this.props.admissionLocationFunctions.getActiveLocationUuid();
        },
        setActiveUuid: (admissionLocationUuid) => {
            this.props.admissionLocationFunctions.setActiveLocationUuid(admissionLocationUuid);
        },
        getAdmissionLocations: () => {
            return this.props.admissionLocationFunctions.getAdmissionLocations();
        }
    };

    cssClass = {
        getTitleClass: () => {
            return this.hierarchyFunction.getActiveUuid() == null ? 'active' : '';
        }
    };

    onClickIcon() {
        this.hierarchyFunction.toggleIsOpen(null, this.props.isOpen);
    }

    onClickTitle() {
        this.hierarchyFunction.setState({
            activeUuid: null,
            isOpen: true,
            activePage: 'listing',
            pageData: {}
        });
    }

    componentWillUpdate(nextProps, nextState) {
        this.higherLevelAdmissionLocations = this.admissionLocationHelper.getHigherLevelAdmissionLocations(
            this.props.admissionLocationFunctions.getAdmissionLocations()
        );
    }

    render() {
        return (
            <div className="left-container">
                <ul>
                    <li className="title">
                        <i className="fa fa-minus-square" onClick={this.onClickIcon} aria-hidden="true" />
                        <span className={this.cssClass.getTitleClass()} onClick={this.onClickTitle}>
                            {this.intl.formatMessage({id: 'ADMISSION_LOCATIONS'})}
                        </span>
                    </li>
                    {Object.keys(this.higherLevelAdmissionLocations).map((uuid) => (
                        <HierarchyItem
                            key={uuid}
                            isParentOpen={this.props.isOpen}
                            hierarchyFunction={this.hierarchyFunction}
                            admissionLocation={this.higherLevelAdmissionLocations[uuid]}
                        />
                    ))}
                </ul>
            </div>
        );
    }
}

LocationHierarchy.propTypes = {
    isOpen: PropTypes.bool.isRequired,
    admissionLocationFunctions: PropTypes.object.isRequired
};

LocationHierarchy.contextTypes = {
    intl: PropTypes.object
};
