package io.m3.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;


/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class ToStringBuilder {

	/**
	 * Default size for the buffer.
	 */
	public static final int DEFAULT_BUFFER = 512;
	
	/**
	 * char buffer for a null object.
	 */
	private static final char[] NULL = new char[] { 'n', 'u', 'l', 'l' };
    
	/**
     * The buffer for this builder.
     */
	private char[] value;
	
	/**
	 * Index for this buffer.
	 */
	private int index;

	/**
	 * boolean to test if we append null value or not.
	 */
    private final boolean appendNull;
    
    private ToStringBuilder(boolean appendNull) {
    	this.value= new char[DEFAULT_BUFFER];
		this.index = 0;
    	this.appendNull = appendNull;
    }

    public ToStringBuilder(String message) {
    	this(message,true);
    }

    public ToStringBuilder(Object object) {
    	this(true);
    	insert(object.getClass().getSimpleName());
        appendIdentityHashCode(object);
        insert('{');
    }
    
    public ToStringBuilder(Object object,boolean appendNull) {
    	this(appendNull);
    	insert(object.getClass().getSimpleName());
        appendIdentityHashCode(object);
        insert('{');
    }
    
    public ToStringBuilder(String message,boolean appendNull) {
    	this(appendNull);
    	insert(message);
        insert('{');
    }

    /**
     * Append.
     *
     * @param message the message
     * @return the to string builder instance.
     */
    public ToStringBuilder append(String message) {
    	insert(message);
        return this;
    }

    /**
     * Append.
     *
     * @param object the object
     * @return the to string builder
     */
    public ToStringBuilder append(Object object) {
        if (object != null) {
        	insert(object.toString());
        }
        return this;
    }

    /**
     * Append.
     *
     * @param key   the key
     * @param value the value
     * @return the to string builder
     */
    public ToStringBuilder append(String key, String value) {
    	if (!appendNull && value == null) {
    		return this;
    	}
    	insert(key);
    	insert("=[").append(value).append("] ");
        return this;
    }
    
    /**
     * Append.
     *
     * @param key   the key
     * @param value the value
     * @return the to string builder
     */
    public ToStringBuilder append(String key, char[] value) {
    	if (!appendNull && value == null) {
    		return this;
    	}
    	insert(key);
    	insert("=[").insert(value == null ? NULL : value).insert("] ");
        return this;
    }

    /**
     * Append.
     *
     * @param key   the key
     * @param value the value
     * @return the to string builder
     */
    public ToStringBuilder append(String key, Object value) {
    	if (!appendNull && value == null) {
    		return this;
    	}
    	insert(key);
    	insert("=[").insert((value == null) ? NULL : value.toString()).insert("] ");
        return this;
    }

    /**
     * Append.
     *
     * @param key    the key
     * @param values the values
     * @return the to string builder
     */
    public ToStringBuilder append(String key, Object[] values) {
    	if (!appendNull && (values == null || values.length == 0)) {
    		return this;
    	}
    	insert(key).insert("=").insert(Arrays.toString(values)).insert(" ");
        return this;
    }

    /**
     * Append.
     *
     * @param key   the key
     * @param value the value
     * @return the to string builder
     */
    public ToStringBuilder append(String key, Long value) {
    	if (!appendNull && value == null) {
    		return this;
    	}
    	insert(key).insert("=[").insert(value).insert("] ");
        return this;
    }

    /**
     * Append.
     *
     * @param key   the key
     * @param value the value
     * @return the to string builder
     */
    public ToStringBuilder append(String key, Integer value) {
    	if (!appendNull && value == null) {
    		return this;
    	}
    	
    	insert(key).insert("=[").insert(value).insert("] ");
        return this;
    }

	/**
     * Append identity hash code.
     *
     * @param object the object
     * @return the to string builder
     */
    public ToStringBuilder appendIdentityHashCode(Object object) {
    	insert("(@").insert(System.identityHashCode(object)).append(")");
        return this;
    }

    /**
     * Append a java.util.Calendar.
     *
     * @param key   the key
     * @param value the value
     * @return the to string builder
     */
    public ToStringBuilder appendDate(String key, LocalDate value) {
    	if (!appendNull && value == null) {
    		return this;
    	}
    	insert(key).insert("=[").append(value == null ? NULL : DateTimeFormatter.ISO_DATE.format(value)).append("] ");
        return this;
    }
    
    /**
     * Append a java.time.LocalTime.
     *
     * @param key   the key
     * @param value the value
     * @return the to string builder
     */
    public ToStringBuilder appendTime(String key, LocalTime value) {
    	if (!appendNull && value == null) {
    		return this;
    	}
    	insert(key).insert("=[").append(value == null ? NULL : DateTimeFormatter.ISO_LOCAL_TIME.format(value)).append("] ");
        return this;
    }

    /**
     * Append.
     *
     * @param key   the key
     * @param value the value
     * @return the to string builder
     */
    public ToStringBuilder appendDateTime(String key, LocalDateTime value) {
    	if (!appendNull && value == null) {
    		return this;
    	}
        insert(key).insert("=[").insert(value == null ? NULL : DateTimeFormatter.ISO_LOCAL_TIME.format(value)).insert("] ");
        return this;
    }
    
    @Override
	public String toString() {
    	if (value[index-1] != '{') {
    		value[index-1] = '}';
    	} else {
    		insert('}');	
    	}
    	String val = new String(value, 0, index);
    	if (index > 1) {
    		value[index-1] = ' ';
    	} else {
			index--;
    	}
    	return val;
	}

	private ToStringBuilder insert(char character) {
    	int newCount = index + 1;
    	if (newCount > value.length) {
			expandCapacity(newCount);
		}
    	value[index++] = character;
    	return this;
    }
    
	private ToStringBuilder insert(String str) {
		int newCount;
		if (str == null) {
			newCount = index + 4;
			if (newCount > value.length) {
				expandCapacity(newCount);
			}
			System.arraycopy(NULL, 0, value, index, 4);
		} else {
			int len = str.length();
			if (len == 0) {
				return this;
			}
			newCount = index + len;
			if (newCount > value.length) {
				expandCapacity(newCount);
			}
			str.getChars(0, len, value, index);
		}
		index = newCount;
		return this;
	}
	
	private ToStringBuilder insert(char[] value) {
		int newCount = index + value.length;
		if (newCount > value.length) {
			expandCapacity(newCount);
		}
		System.arraycopy(value, 0, this.value, index, value.length);
		index = newCount;
		return this;
	}
	
	private ToStringBuilder insert(Object value) {
		if (value == null) {
			insert(NULL);
		} else {
			insert(value.toString());	
		}
		return this;
	}

	private void expandCapacity(int minimumCapacity) {
		int newCapacity = (value.length + 1) * 2;
		if (newCapacity < 0) {
			newCapacity = Integer.MAX_VALUE;
		} else if (minimumCapacity > newCapacity) {
			newCapacity = minimumCapacity;
		}
		value = Arrays.copyOf(value, newCapacity);
	}
	
	

}