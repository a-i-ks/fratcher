import React from "react";
import {TagCloud} from "react-tagcloud";

class InterestsTagCloud extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        return (
            <div>
                {this.props.data ?
                    <TagCloud minSize={12}
                              maxSize={35}
                              tags={this.props.data}/> : ""}
            </div>)
    }
}

export default InterestsTagCloud;
