import React from 'react';
import PropTypes from 'prop-types';

import AdmissionLocationHelper from 'utilities/admissionLocationHelper';

export default class HierarchyItem extends React.Component {
    constructor(props) {
        super(props);

        this.admissionLocationHelper = new AdmissionLocationHelper();

        this.childAdmissionLocations = this.admissionLocationHelper.getChildAdmissionLocations(
            this.props.hierarchyFunction.getAdmissionLocations(),
            this.props.admissionLocation.uuid
        );

        this.onClickIcon = this.onClickIcon.bind(this);
        this.onClickTitle = this.onClickTitle.bind(this);
    }

    componentWillUpdate(nextProps, nextState) {
        this.childAdmissionLocations = this.admissionLocationHelper.getChildAdmissionLocations(
            this.props.hierarchyFunction.getAdmissionLocations(),
            this.props.admissionLocation.uuid
        );
    }

    cssClass = {
        getIconClass: () => {
            return Object.keys(this.childAdmissionLocations).length == 0
                ? 'fa fa-caret-right'
                : this.props.admissionLocation.isOpen ? 'fa fa-minus-square' : 'fa fa-plus-square';
        },
        getTitleClass: () => {
            return this.props.hierarchyFunction.getActiveUuid() == this.props.admissionLocation.uuid ? 'active' : '';
        },
        getItemClass: () => {
            return !this.props.isParentOpen ? 'hide' : '';
        }
    };

    onClickIcon() {
        this.props.hierarchyFunction.toggleIsOpen(
            this.props.admissionLocation.uuid,
            this.props.admissionLocation.isOpen
        );
    }

    onClickTitle() {
        this.props.hierarchyFunction.setAdmissionLocationIsOpen(this.props.admissionLocation.uuid, true);
        this.props.hierarchyFunction.setState({
            activeUuid: this.props.admissionLocation.uuid,
            activePage: 'listing',
            pageData: {}
        });
    }

    render() {
        return (
            <li className={this.cssClass.getItemClass()}>
                <ul>
                    <li>
                        <i className={this.cssClass.getIconClass()} onClick={this.onClickIcon} aria-hidden="true" />
                        <span className={this.cssClass.getTitleClass()} onClick={this.onClickTitle}>
                            {' '}
                            {this.props.admissionLocation.name}{' '}
                        </span>
                    </li>
                    {Object.keys(this.childAdmissionLocations).map((uuid) => (
                        <HierarchyItem
                            key={uuid}
                            isParentOpen={this.props.admissionLocation.isOpen}
                            hierarchyFunction={this.props.hierarchyFunction}
                            admissionLocation={this.childAdmissionLocations[uuid]}
                        />
                    ))}
                </ul>
            </li>
        );
    }
}

HierarchyItem.propTypes = {
    isParentOpen: PropTypes.bool.isRequired,
    hierarchyFunction: PropTypes.object.isRequired,
    admissionLocation: PropTypes.shape({
        name: PropTypes.string.isRequired,
        uuid: PropTypes.string.isRequired,
        parentAdmissionLocationUuid: PropTypes.string,
        isOpen: PropTypes.bool.isRequired,
        isHigherLevel: PropTypes.bool.isRequired
    })
};
