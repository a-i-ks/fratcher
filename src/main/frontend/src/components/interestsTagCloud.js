import React from "react";
import {TagCloud} from "react-tagcloud";

class InterestsTagCloud extends React.Component {
    constructor(props) {
        super(props);
    }

    componentWillMount() {
        if (this.props.data !== null) {
            const tmpKeywords = this.props.data.map(function (keyword) {
                return {value: keyword.value, count: keyword.count, key: keyword.id};
            });
            this.setState({
                keywords: tmpKeywords
            });
        } else {
            this.setState({
                keywords: null
            });
        }
    }

    render() {
        return (
            <div>
                {this.state.keywords ?
                    <TagCloud minSize={12}
                              maxSize={35}
                              tags={this.state.keywords}/> :
                    <div/>}
            </div>)
    }
}

export default InterestsTagCloud;
