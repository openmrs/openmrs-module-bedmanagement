import React from 'react';
import {Link} from 'react-router-dom';
import UrlHelper from 'utilities/urlHelper';

require('./header.css');
class Header extends React.PureComponent {
    urlHelper = new UrlHelper();

    linkClass = (path) => {
        return this.props.path == path ? 'active' : '';
    };

    render() {
        return (
            <nav>
                <ul className="title-section">
                    <li>
                        <a href={this.urlHelper.originPath() + '/openmrs'}>
                            <i className="fa fa-home" aria-hidden="true" />
                        </a>
                    </li>
                    <li>
                        <Link
                            to={this.urlHelper.owaPath() + '/admissionLocations.html'}
                            className={this.linkClass(this.urlHelper.owaPath() + '/admissionLocations.html')}>
                            Admission Locations
                        </Link>
                    </li>
                    <li>
                        <Link
                            to={this.urlHelper.owaPath() + '/bedTypes.html'}
                            className={this.linkClass(this.urlHelper.owaPath() + '/bedTypes.html')}>
                            Bed Types
                        </Link>
                    </li>
                    <li>
                        <Link
                            to={this.urlHelper.owaPath() + '/bedTags.html'}
                            className={this.linkClass(this.urlHelper.owaPath() + '/bedTags.html')}>
                            Bed Tags
                        </Link>
                    </li>
                </ul>
            </nav>
        );
    }
}

export default Header;
