import React from 'react';
import {Link} from 'react-router-dom';
import UrlHelper from '../../utilities/urlHelper';

require('./header.css');
const urlHelper = new UrlHelper();
class Header extends React.PureComponent {

    linkClass = (path) => {
        return this.props.path == path ? 'active' : '';
    };

    render() {
        return <nav>
            <ul className="title-section">
                <li>
                    <Link to={urlHelper.owaPath() + '/admissionLocations.html'}
                        className={this.linkClass(urlHelper.owaPath() + '/admissionLocations.html')}>
                        Admission Locations
                    </Link>
                </li>
                <li>
                    <Link to={urlHelper.owaPath() + '/bedTypes.html'}
                        className={this.linkClass(urlHelper.owaPath() + '/bedTypes.html')}>
                        Bed Types
                    </Link>
                </li>
                <li>
                    <Link to={urlHelper.owaPath() + '/bedTags.html'}
                        className={this.linkClass(urlHelper.owaPath() + '/bedTags.html')}>
                        Bed Tags
                    </Link>
                </li>
            </ul>
        </nav>;
    }
}

export default Header;