package org.chunkmancompletionist.tasks;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;

@Slf4j
@Getter @Setter
public class MapMovement extends ArrayList<MapCoordinate> {
    public MapMovement() {}

    public boolean includes(MapMovement requirement) {
        if(requirement.size() > this.size())
            return false;

        int count = requirement.size();
        for(int index = 0; index < count; index++) {
            if(requirement.get(count - 1 - index).equals(this.get(this.size() - 1 - index)))
                continue;

            return false;
        }

        return true;
    }
}
