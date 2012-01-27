# Usage

## Bean Property Expression Enricher

Enriches a payload Java object property.

### Example

```xml
<mule xmlns="http://www.mulesoft.org/schema/mule/core"
      ...
      xsi:schemaLocation="
        ...
        http://www.mulesoft.org/schema/mule/core http://www.mulesoft.org/schema/mule/core/3.2/mule.xsd">

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
      xmlns:ricston="http://www.ricston.com/schema/mule/ricston"
      ...
      xsi:schemaLocation="
        ...
        http://www.mulesoft.org/schema/mule/core http://www.mulesoft.org/schema/mule/core/3.2/mule.xsd
        http://www.ricston.com/schema/mule/ricston http://www.ricston.com/schema/mule/ricston/3.2/mule-ricston.xsd">

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
      xmlns:ricston="http://www.ricston.com/schema/mule/ricston"
      ...
      xsi:schemaLocation="
        ...
        http://www.mulesoft.org/schema/mule/core http://www.mulesoft.org/schema/mule/core/3.2/mule.xsd
        http://www.ricston.com/schema/mule/ricston http://www.ricston.com/schema/mule/ricston/3.2/mule-ricston.xsd">

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

## Exception Message Processor Chain

Invokes the next message processor in the flow. If any of the subsequent messages processors throws an exception, the
Exception Message Processor Chain picks up the exception, creates an exception message (similar behaviour to the
classical exception handler) and then applies the chain of nested processors which are configured on it.

### Example

```xml
<mule xmlns="http://www.mulesoft.org/schema/mule/core"
      xmlns:ricston="http://www.ricston.com/schema/mule/ricston"
      ...
      xsi:schemaLocation="
        ...
        http://www.mulesoft.org/schema/mule/core http://www.mulesoft.org/schema/mule/core/3.2/mule.xsd
        http://www.ricston.com/schema/mule/ricston http://www.ricston.com/schema/mule/ricston/3.2/mule-ricston.xsd">

    <flow name="ExceptionMessageProcessorChain">
        ...
        <ricston:exception-message-processor-chain>
           <expression-transformer evaluator="groovy" expression="return 'Sad Path'"/>
        </ricston:exception-message-processor-chain>
        ...
    </flow>
    ...
</mule>
```

## Transaction Aware Object Store

An object store which can join an XA transaction. This could be used, for example, to make the idempotent message filter
participate in transactions.

### Example

```xml
<mule xmlns="http://www.mulesoft.org/schema/mule/core"
      ...
      xsi:schemaLocation="
        ...
        http://www.mulesoft.org/schema/mule/core http://www.mulesoft.org/schema/mule/core/3.2/mule.xsd">

    <jbossts:transaction-manager/>

    <flow name="TransactionAwareObjectStore">
        <vm:inbound-endpoint path="in" exchange-pattern="one-way">
            <xa-transaction action="BEGIN_OR_JOIN"/>
        </vm:inbound-endpoint>
        <idempotent-message-filter>
            <custom-object-store class="org.mule.module.ricston.objectstore.TransactionAwareObjectStore"/>
        </idempotent-message-filter>
        <test:component appendString=" Received"/>
        <vm:outbound-endpoint path="out" exchange-pattern="one-way">
            <xa-transaction action="ALWAYS_JOIN"/>
        </vm:outbound-endpoint>
    </flow>
    ...
</mule>
```
