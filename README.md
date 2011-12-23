# Usage

## Bean Property Expression Enricher

Enriches a payload Java object property.            s

### Example

```xml
<mule xmlns="http://www.mulesoft.org/schema/mule/core"
      xmlns:ricston="http://www.mulesoft.org/schema/mule/ricston"
      ...
      xsi:schemaLocation="
        ...
        http://www.mulesoft.org/schema/mule/ricston http://www.mulesoft.org/schema/mule/ricston/3.2/mule-ricston.xsd">

    <flow name="Enrich">
        ...
        <enricher target="#[bean-property:apple.washed]">
            <vm:outbound-endpoint path="status" exchange-pattern="request-response"/>
        </enricher>
        ...
    </flow>
    ...
</mule>
```

## Ignore Reply All

Sends a message to each nested message processor while ignoring their replies. The Ignore Reply All forwards the original
message to the next message processor. Note that in case of exceptions from the nested message processors, the exception
payload is copied onto the original message.

### Example

```xml
<mule xmlns="http://www.mulesoft.org/schema/mule/core"
      xmlns:ricston="http://www.mulesoft.org/schema/mule/ricston"
      ...
      xsi:schemaLocation="
        ...
        http://www.mulesoft.org/schema/mule/ricston http://www.mulesoft.org/schema/mule/ricston/3.2/mule-ricston.xsd">

    <flow name="IgnoreAll">
        ...
        <ricston:ignore-reply-all>
            <vm:outbound-endpoint path="sync.in" exchange-pattern="request-response" transformer-refs="appendTest1"/>
            <vm:outbound-endpoint path="sync.exception.in" exchange-pattern="request-response"
                                  transformer-refs="appendTest2"/>
        </ricston:ignore-reply-all>
        ...
    </flow>
    ...
</mule>
```

## Ignore Reply Pass Through

Sends a message to a nested message processor while ignoring its reply. The Ignore Reply Pass Through forwards the original
message to the next message processor. Note that in case of exceptions from the nested message processor, the exception
payload is copied onto the original message.

### Example

```xml
<mule xmlns="http://www.mulesoft.org/schema/mule/core"
      xmlns:ricston="http://www.mulesoft.org/schema/mule/ricston"
      ...
      xsi:schemaLocation="
        ...
        http://www.mulesoft.org/schema/mule/ricston http://www.mulesoft.org/schema/mule/ricston/3.2/mule-ricston.xsd">

    <flow name="Ignore">
        ...
        <ricston:ignore-reply-pass-through>
            <vm:outbound-endpoint path="sync.in" exchange-pattern="request-response" transformer-refs="appendTest1"/>
        </ricston:ignore-reply-pass-through>
        ...
    </flow>
    ...
</mule>
```