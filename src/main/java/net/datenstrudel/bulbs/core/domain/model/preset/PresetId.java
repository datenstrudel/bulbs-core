package net.datenstrudel.bulbs.core.domain.model.preset;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;
import net.datenstrudel.bulbs.shared.domain.model.ValueObject;

import javax.validation.ValidationException;
import java.io.Serializable;
import java.util.Objects;

/**
 * @author Thomas Wendzinski
 * @version 1.0
 * @created 08-Jun-2013 22:51:42
 */
public class PresetId implements ValueObject<PresetId>, Serializable {

    private String presetUuid;
    private BulbsContextUserId creator;

	private PresetId(){}
    public PresetId(String presetUuid, BulbsContextUserId creator) {
        this.presetUuid = presetUuid;
        this.creator = creator;
    }

    public String getPresetUuid() {
        return presetUuid;
    }
    public BulbsContextUserId getCreator() {
        return creator;
    }

    public String serialize(){
        return presetUuid + creator.getUuid();
    }
    public static PresetId fromSerialized(String in){
        if(in.length() < 37)throw new ValidationException("Id was too short");
        String presetUuId = in.substring(0, 36);
        String userId = in.substring(36);
        return new PresetId(presetUuId, new BulbsContextUserId(userId));
    }
    
    @Override
    public boolean sameValueAs(PresetId other) {
        if(other == null)return false;
        if(!this.presetUuid.equals(other.presetUuid))return false;
        if(!this.creator.equals(other.creator))return false;
        return true;
    }
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 41 * hash + Objects.hashCode(this.presetUuid);
        hash = 41 * hash + Objects.hashCode(this.creator);
        return hash;
    }
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PresetId other = (PresetId) obj;
        return this.sameValueAs(other);
    }
    @Override
    public String toString() {
        return "PresetId{" + "presetUuid=" + presetUuid + ", creator=" + creator + '}';
    }
}