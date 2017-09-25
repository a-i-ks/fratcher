import React from "react";
import {TagCloud} from "react-tagcloud";

class InterestsTagCloud extends React.Component {
    constructor(props) {
        super(props);
    }

    componentWillMount() {
        console.log("componentWillMount()");
        console.log(this.props.data);
        if (this.props.data !== null) {
            console.log("data != null");
            const tmpKeywords = this.props.data.map(function (keyword) {
                return {value: keyword.value, count: keyword.count, key: keyword.id};
            });
            console.log(tmpKeywords);
            this.setState({
                keywords: tmpKeywords
            });
            console.log("after ..");
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
